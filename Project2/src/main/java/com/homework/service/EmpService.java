/**
 * 
 */
package com.homework.service;

import java.util.List;
import java.util.Map;

public interface EmpService {
	
	Map<String, Object> query1();
	
	List<Map<String, Object>> query2();
	
	List<Map<String, Object>> query3();
	
	List<Map<String, Object>> query4();
	
	List<Map<String, Object>> query5(String e1,String e2);
	
	List<Map<String, Object>> query6(String e1,String e2,String e3);
	
}
