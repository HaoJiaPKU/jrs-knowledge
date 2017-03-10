package cn.edu.pku.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.edu.pku.hyp.Hyponymy;
import cn.edu.pku.hyp.HyponymyObj;
import cn.edu.pku.hyp.TreeNode;

@Controller
@RequestMapping("knowledge")
public class KnowledgeController {

	@RequestMapping(value = "/load", method = RequestMethod.POST)
	public @ResponseBody ArrayList<TreeNode> load(
			HttpServletRequest request, HttpServletResponse response) {
		String fileName = request.getParameter("fileName");
		ArrayList<TreeNode> list = new ArrayList<TreeNode>();
		Hyponymy hyponymy = new Hyponymy();
		hyponymy.load("knowledge/" + fileName);
		for (HyponymyObj hyp : hyponymy.hypDict) {
			TreeNode node = new TreeNode(hyp.hyponym, hyp.hypernym, hyp.hyponym);
			list.add(node);
		}
		return list;
	}
	
	@RequestMapping(value = "/open", method = RequestMethod.POST)
	public @ResponseBody ArrayList<String> open(
			HttpServletRequest request, HttpServletResponse response) {
		ArrayList<String> list = new ArrayList<String>();
		File file = new File("knowledge");
		for (File f : file.listFiles()) {
			list.add(f.getName());
		}
		return list;
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public @ResponseBody String save(
			HttpServletRequest request, HttpServletResponse response) {
		String fileName = request.getParameter("fileName");
		String list = request.getParameter("zNodes");
		Hyponymy hyponymy = new Hyponymy();
		JSONArray arr = JSONObject.parseArray(list);
		for (int i = 0; i < arr.size(); i ++) {
			JSONObject obj = JSONObject.parseObject(arr.get(i).toString());
			HyponymyObj hyp = new HyponymyObj(obj.getString("pId"), obj.getString("name"), "");
			hyponymy.hypDict.add(hyp);
		}
		hyponymy.save("knowledge/" + fileName);
		return null;
	}
}
