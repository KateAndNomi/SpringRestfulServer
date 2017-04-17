package com.dmc.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.dmc.bean.ReimbInfo;
import com.dmc.bean.ReimbInfoQuery;

public class JdbcDaoImp {
	private DataSource datasource;
	private JdbcTemplate jdbcTemplateObject;
	private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void setdatasource(DataSource ds) {
		this.datasource = ds;
		this.jdbcTemplateObject = new JdbcTemplate(datasource);
	}

	public List<Map<String, Object>> getAllUsers() {
		List<Map<String, Object>> results = jdbcTemplateObject.queryForList("select * from USER");
		System.out.println("返回的数量是:" + results.size());
		return results;
	}

	/**
	 * REIMBURSE 插入一个报销信息
	 * 
	 * @param info
	 * @return
	 */
	public boolean insertReimbInfo(ReimbInfo info) {
		String sql = "INSERT INTO REIMBURSE (name,description,amount,user,apply_time,time,type) values (?,?,?,?,?,?,?)";
		int count = jdbcTemplateObject.update(sql,
				new Object[] { info.getTitle(), info.getDesc(), Double.valueOf(info.getAmount()), info.getUser(),
						sf.format(new Date()), info.getCostTime(), info.getType() });
		return count == 1 ? true : false;
	}

	/**
	 * REIMBURSE 分页查询某用户的报销信息
	 * 
	 * @param user
	 * @param page
	 * @return
	 */
	public List<ReimbInfoQuery> getReimInfo(int user, int state, int page) {
		// 10 records per page.
		List<ReimbInfoQuery> results = new ArrayList<>();
		List<Map<String, Object>> reimbs = null;
		switch (state) {
		case -1:
		default: {
			// 全量搜索
			String sql = "SELECT * FROM REIMBURSE WHERE user=? AND is_valid=1 LIMIT ?,10";
			reimbs = jdbcTemplateObject.queryForList(sql, new Object[] { user, page * 10 });
			break;
		}
		case 0:
		case 1:
		case 2: {
			// 条件搜索
			String sql = "SELECT * FROM REIMBURSE WHERE user=? AND is_valid=1 AND state=? LIMIT ?,10";
			reimbs = jdbcTemplateObject.queryForList(sql, new Object[] { user, state, page * 10 });
			break;
		}

		}
		List<String> reimbIDs = new ArrayList<>(reimbs.size());
		for (Map<String, Object> map : reimbs) {
			ReimbInfoQuery bean = new ReimbInfoQuery();
			bean.setAmount(map.get("amount").toString());
			bean.setCostTime(map.get("time").toString());
			bean.setDesc(map.get("description").toString());
			bean.setId(map.get("id").toString());
			bean.setState(map.get("state").toString());
			bean.setTime(map.get("apply_time").toString());
			bean.setTitle(map.get("name").toString());
			bean.setType(map.get("type").toString());
			results.add(bean);
			reimbIDs.add(map.get("id").toString());
		}
		// 获取资源
		if (reimbIDs.size() != 0) {
			String sql = "SELECT L.obj_id,C.name FROM RES_RELATION L LEFT JOIN RESOURCE C ON L.res_id = C.id "
					+ "WHERE L.type = 'REIM_PIC' AND L.obj_id IN " + inClause(reimbIDs) + " AND C.is_valid=1";
			System.out.println(sql);
			List<Map<String, Object>> resources = jdbcTemplateObject.queryForList(sql);
			for (Map<String, Object> map : resources) {
				String id = map.get("obj_id").toString();
				String res = map.get("name").toString();
				for (ReimbInfoQuery r : results) {
					if (r.getId().equals(id)) {
						r.getImgs().add(res);
						break;
					}
				}
			}
		}

		return results;
	}

	/**
	 * 构造SQL中的in子句
	 * 
	 * @param aa
	 * @return
	 */
	private String inClause(List<? extends Object> aa) {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for (Object a : aa) {
			sb.append(a.toString()).append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append(")");
		return sb.toString();
	}

}