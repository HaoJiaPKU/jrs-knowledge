package cn.edu.pku.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("knowledge")
public class IndexController {

	@RequestMapping(value = "/load", method = RequestMethod.POST)
	public @ResponseBody HashMap<String,Object> createPositionIndex(
			HttpServletRequest request, HttpServletResponse response) {
		System.out.println("into load");
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("msg", "失败");
		return map;
	}
}
