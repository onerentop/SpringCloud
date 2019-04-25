package org.vista.ecms.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {
	@RequestMapping(value = "/read")
	public String read(HttpServletRequest request) {
		// 读取文件中已有的url
		Properties propp = new Properties();
		String oldUrl = "";
		try {
			InputStream ins = new FileInputStream(new File("F:\\config.properties"));
			propp.load(ins);
			oldUrl = propp.getProperty("url");
			System.out.println("oldUrl---" + oldUrl);
			ins.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "readcallback(\"" + oldUrl + "\")";
	}
}
