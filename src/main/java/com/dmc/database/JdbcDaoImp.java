package com.dmc.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.alibaba.fastjson.JSON;
import com.dmc.bean.Order;
import com.dmc.bean.OrderItem;
import com.dmc.bean.ReimbInfo;
import com.dmc.bean.ReimbInfoQuery;

public class JdbcDaoImp {
	private DataSource datasource;
	private JdbcTemplate jdbcTemplateObject;
	private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sf2 = new SimpleDateFormat("yyyyMMddHHmmss");

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
	public boolean insertReimbInfo(ReimbInfo info, String generateID) {
		String sql = "INSERT INTO REIMBURSE (name,description,amount,user,apply_time,time,type,reimb_id) values (?,?,?,?,?,?,?,?)";
		int count = jdbcTemplateObject
				.update(sql,
						new Object[] { info.getTitle(), info.getDesc(), Double.valueOf(info.getAmount()),
								info.getUser(), sf.format(new Date()), info.getCostTime(), info.getType(),
								generateID });
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

	/**
	 * 转换null为""
	 * 
	 * @param l
	 * @return
	 */
	private List<Map<String, Object>> null2Empty(List<Map<String, Object>> l) {
		for (Map<String, Object> map : l) {
			for (String s : map.keySet()) {
				if (map.get(s) == null) {
					map.put(s, "");
				}
			}
		}
		return l;
	}

	/**
	 * 注册资源
	 * 
	 * @param name
	 *            资源名称
	 * @param path
	 *            资源路径
	 * @return count
	 */
	public int registerResource(String name, String path, String type, String obj_id) {
		String sql = "INSERT INTO RESOURCE (name,path,type,obj_id) VALUES (?,?,?,?)";
		return jdbcTemplateObject.update(sql, new Object[] { name, path, type, obj_id });
	}

	/**
	 * 根据关键字,排序模式(状态/时间),页码来查询订单数据
	 * 
	 * @param page
	 * @param mode
	 * @param keyword
	 * @return
	 */
	public List<Order> queryOrdersByPage(int page, int mode, String keyword) {
		List<Order> results = new ArrayList<>();
		// 1.查询该分页中符合关键字要求的order_id...因为mysql不支持limit的子句查询,要拆分
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT O.order_id FROM `ORDER` O");
		sql.append(" LEFT JOIN COOPERATION C ON C.c_id = O.id");
		sql.append(" LEFT JOIN ORDER_ITEM I ON O.order_id = I.i_order_id");
		sql.append(" LEFT JOIN METERIAL M ON I.i_meterial_id = M.m_id");
		// keyword
		if (keyword != null && !keyword.equals("")) {
			sql.append(" WHERE O.description LIKE '%").append(keyword).append("%'");
			sql.append(" OR O.order_id LIKE '%").append(keyword).append("%'");
			sql.append(" OR C.`c_name` LIKE '%").append(keyword).append("%'");
			sql.append(" OR M.`m_name` LIKE '%").append(keyword).append("%'");
		}
		// sort mode
		switch (mode) {
		case 1:
		default:
			sql.append(" ORDER BY O.build_time DESC");
			break;
		case 2:
			sql.append(" ORDER BY O.state,O.build_time DESC");
		}
		sql.append(" LIMIT ?,20");
		System.out.println(sql.toString());
		List<Map<String, Object>> ids_q = jdbcTemplateObject.queryForList(sql.toString(), new Object[] { page });
		List<String> ids = new ArrayList<>();
		for (Map<String, Object> map : ids_q) {
			ids.add((String) map.get("order_id"));
		}
		System.out.println(ids.toString());

		// 2.根据查询详细数据
		StringBuffer sql2 = new StringBuffer();
		sql2.append("SELECT * FROM `ORDER` O");
		sql2.append(" LEFT JOIN COOPERATION C ON C.c_id = O.id");
		sql2.append(" LEFT JOIN ORDER_ITEM I ON O.order_id = I.i_order_id");
		sql2.append(" LEFT JOIN METERIAL M ON I.i_meterial_id = M.m_id");
		sql2.append(" WHERE O.order_id in ").append(inClause(ids));
		switch (mode) {
		case 1:
		default:
			sql2.append(" ORDER BY O.build_time DESC");
			break;
		case 2:
			sql2.append(" ORDER BY O.state,O.build_time DESC");
		}
		System.out.println(sql2.toString());
		List<Map<String, Object>> results_q = jdbcTemplateObject.queryForList(sql2.toString());
		results_q = null2Empty(results_q);
		System.out.println("查出来的数据:" + results_q.size());

		// 3.分析数据生成Bean
		System.out.println(results_q.toString());
		Map<String, Order> orderMap = new LinkedHashMap<>();
		for (Map<String, Object> map : results_q) {
			String oid = (String) map.get("order_id");
			System.out.println(oid + ":" + map.get("build_time").toString());
			Order order = null;
			if (!orderMap.containsKey(oid)) {
				// 新的Order
				orderMap.put(oid, new Order());
				order = orderMap.get(oid);

				// 新增信息
				order.setArrived_time(map.get("arrived_time").toString());
				order.setBuild_time(map.get("build_time").toString());
				order.setCooperation(map.get("c_name").toString());
				order.setCooperation_id(map.get("cooperation").toString());
				order.setDescription(map.get("description").toString());
				order.setOrder_id(oid);
				order.setReceiver(map.get("receiver").toString());
				order.setReceiver_phone(map.get("receiver_phone").toString());
				order.setState(map.get("state").toString());
				order.setTotal(map.get("total").toString());
			} else {
				order = orderMap.get(oid);
			}
			// 新增item
			OrderItem item = new OrderItem();
			item.setCount_type(map.get("i_count_type").toString());
			item.setDescription(map.get("i_description").toString());
			item.setMeterial(map.get("m_name").toString());
			item.setMeterial_id(map.get("i_meterial_id").toString());
			item.setNumber(map.get("i_number").toString());
			item.setOrder_id(map.get("i_order_id").toString());
			item.setPattern_res(map.get("i_pattern_res").toString());
			item.setSize(map.get("i_size").toString());
			item.setTotal_price(map.get("i_total_prize").toString());
			item.setUnit_price(map.get("i_unit_prize").toString());
			order.getItems().add(item);
		}
		for (String oid : orderMap.keySet()) {
			results.add(orderMap.get(oid));
		}
		return results;
	}

}