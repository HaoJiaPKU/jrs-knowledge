package cn.edu.pku.controller;

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

import cn.edu.pku.hyp.Hyponymy;
import cn.edu.pku.hyp.HyponymyObj;
import cn.edu.pku.hyp.TreeNode;

@Controller
@RequestMapping("knowledge")
public class KnowledgeController {

	@RequestMapping(value = "/load", method = RequestMethod.POST)
	public @ResponseBody ArrayList<TreeNode> createPositionIndex(
			HttpServletRequest request, HttpServletResponse response) {
		ArrayList<TreeNode> list = new ArrayList<TreeNode>();
		Hyponymy hyponymy = new Hyponymy();
		hyponymy.load("hyp.txt");
		for (HyponymyObj hyp : hyponymy.hypDict) {
			TreeNode node = new TreeNode(hyp.hypernym, hyp.hyponym, hyp.hypernym);
			list.add(node);
		}
		return list;
	}
}
