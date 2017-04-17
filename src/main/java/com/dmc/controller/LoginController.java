package com.dmc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.dmc.bean.LoginMsg;
import com.dmc.bean.ReimbInfo;
import com.dmc.bean.ReimbInfoQuery;
import com.dmc.database.JdbcDaoImp;

@RestController
public class LoginController {
	private static final String UPLOAD_DIR = "./UploadRes";
	private JdbcDaoImp dao;

	public LoginController() {
		System.out.println("虽然是反射...但也走了构造方法的....");
		System.out.println(System.getProperty("user.dir"));
		ApplicationContext context = new FileSystemXmlApplicationContext("src/main/xml/JdbcConfig.xml");
		dao = (JdbcDaoImp) context.getBean("jdbcDaoImp");
		System.out.println("MAXX数据库配置完成,开始测试打印------->");
		List<Map<String, Object>> results = dao.getAllUsers();
		for (Map<String, Object> map : results) {
			String name = (String) map.get("name");
			System.out.println(name);
		}
		System.out.println("<--------测试完成");
	}

	/**
	 * 测试用
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping("/login")
	public LoginMsg login(@RequestParam(value = "name", defaultValue = "World") String name) {
		List<Map<String, Object>> results = dao.getAllUsers();
		StringBuffer stringBuffer = new StringBuffer();
		for (Map<String, Object> map : results) {
			String a = (String) map.get("name");
			stringBuffer.append(a + "/");
		}
		return new LoginMsg(name, "Good Morning!" + stringBuffer.toString());
	}

	/**
	 * 上传资源接口
	 * 
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String uploadpdf(@RequestParam("file") MultipartFile file) {
		if (!file.isEmpty()) {
			try {
				File dir = new File(UPLOAD_DIR);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				byte[] bytes = file.getBytes();
				FileOutputStream fileOutputStream = new FileOutputStream(
						new File(UPLOAD_DIR + "/" + file.getOriginalFilename()));
				fileOutputStream.write(bytes);
				fileOutputStream.close();
				return "You successfully uploaded,size:" + bytes.length;
			} catch (Exception e) {
				return "You failed to upload";
			}
		} else {
			System.out.println("File empty");
			return "You failed to upload because the file was empty.";
		}
	}

	/**
	 * 下载二进制资源接口
	 * 
	 * @param filename
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/img")
	public void getImageSource(@RequestParam("filename") String filename, HttpServletResponse response)
			throws IOException {
		OutputStream os = response.getOutputStream();
		try {
			File file = new File(UPLOAD_DIR + "/" + filename);
			if (!file.exists()) {
				response.setStatus(404);
				return;
			}
			String type = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.setContentType("image/" + type + "; charset=utf-8");
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] bytes = new byte[fileInputStream.available()];
			fileInputStream.read(bytes);
			fileInputStream.close();
			os.write(bytes);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				os.close();
			}

		}
	}

	/**
	 * 申请报销接口
	 * 
	 * @param info
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/applyReimburse", method = RequestMethod.POST)
	public @ResponseBody String applyReimburse(@RequestParam(value = "info") String info,
			HttpServletResponse response) {
		System.out.println(info);
		try {
			ReimbInfo infoObj = JSON.parseObject(info, ReimbInfo.class);
			System.out.println(infoObj.toString());
			if (dao.insertReimbInfo(infoObj)) {
				response.setStatus(200);
				return "";
			} else {
				response.setStatus(400);
				return "";
			}
		} catch (Exception e) {
			response.setStatus(400);
			return "";
		}
	}

	/**
	 * 分页获取某个用户的报销信息
	 * 
	 * @param user
	 * @param page
	 * @return
	 */
	@RequestMapping("/getReimInfo")
	public String getReimInfo(@RequestParam(value = "user") String user, @RequestParam(value = "page") String page,
			@RequestParam(value = "mode") String mode) {
		List<ReimbInfoQuery> results = dao.getReimInfo(Integer.valueOf(user), Integer.valueOf(mode),
				Integer.valueOf(page));
		String json = JSON.toJSONString(results);
		return json;
	}

}
