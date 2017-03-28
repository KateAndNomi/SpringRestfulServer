package com.dmc.controller;

import java.io.File;
import java.io.FileOutputStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dmc.bean.LoginMsg;

@RestController
public class LoginController {
	private static final String UPLOAD_DIR = "./UploadRes";

	@RequestMapping("/login")
	public LoginMsg login(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new LoginMsg(name, "Good Morning!");
	}

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

}
