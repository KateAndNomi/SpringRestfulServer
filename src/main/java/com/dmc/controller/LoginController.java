package com.dmc.controller;

import java.io.BufferedOutputStream;
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
	@RequestMapping("/login")
	public LoginMsg login(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new LoginMsg(name, "Good Morning!");
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload(@RequestParam(value = "name", defaultValue = "Biubiubiu") String name,
			@RequestParam("file") MultipartFile file) {
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(name + "-uploaded")));
				stream.write(bytes);
				stream.close();
				return "You successfully uploaded " + name + " into " + name + "-uploaded !";
			} catch (Exception e) {
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		} else {
			return "You failed to upload " + name + " because the file was empty.";
		}
	}

	@RequestMapping(value = "/uploadpdf", method = RequestMethod.POST)
	public @ResponseBody String uploadpdf(@RequestParam("file") MultipartFile file) {
		System.out.println("!!!!!!!!!");
		if (!file.isEmpty()) {
			System.out.println("File not empty");
			try {
				byte[] bytes = file.getBytes();
				FileOutputStream fileOutputStream = new FileOutputStream(new File("/Users/maxx/Desktop/doraemon2.pdf"));
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

	@RequestMapping(value = "/hahaha", method = RequestMethod.POST)
	public LoginMsg hahaha(@RequestParam(value = "name", defaultValue = "Biubiubiu") String name) {
		return new LoginMsg(name, "Good Evening!");
	}

}
