/**
 * 
 */
package com.homework.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.homework.service.EmpService;

@Controller
public class EmpController {

	@Autowired
	EmpService empService;
	
	@RequestMapping("/")
	public String query(Model model) {
		Map<String, Object> query1 = empService.query1();
		List<Map<String, Object>> query2 = empService.query2();
		List<Map<String, Object>> query3 = empService.query3();
		List<Map<String, Object>> query4 = empService.query4();
		model.addAttribute("query1", query1);
		model.addAttribute("query2", query2);
		model.addAttribute("query3", query3);
		model.addAttribute("query4", query4);
		
		return "index";
	}
	
	@RequestMapping("/separate")
	@ResponseBody
	public String separate(String type,String e1,String e2,String e3) {
		if (type.equals("1")) {
			List<Map<String, Object>> query5 = empService.query5(e1,e2);
			if (query5.size() > 0 ) {
				return "1 degree of separation success！ ";
			}else {
				return "1 degree of separation error！ ";
			}
		}else {
			List<Map<String, Object>> query6 = empService.query6(e1,e2,e3);
			if (query6.size() > 0 ) {
				return "2 degree of separation success ！";
			}else {
				return "2 degree of separation error ！ ";
			}
		}
	}
	
}
