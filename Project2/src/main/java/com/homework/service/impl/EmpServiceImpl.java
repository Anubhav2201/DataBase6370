/**
 * 
 */
package com.homework.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.homework.service.EmpService;
import com.homework.sql.SqlString;

@Service
public class EmpServiceImpl implements EmpService{
	
	@Autowired
	JdbcTemplate jdbc;

	@Override
	public Map<String, Object> query1() {
		List<Map<String, Object>> queryForList = jdbc.queryForList(SqlString.query1);
		return queryForList.get(0);
	}

	@Override
	public List<Map<String, Object>> query2() {
		List<Map<String, Object>> queryForList = jdbc.queryForList(SqlString.query2);
		return queryForList;
	}

	@Override
	public List<Map<String, Object>> query3() {
		List<Map<String, Object>> queryForList = jdbc.queryForList(SqlString.query3);
		return queryForList;
	}

	@Override
	public List<Map<String, Object>> query4() {
		List<Map<String, Object>> queryForList = jdbc.queryForList(SqlString.query4);
		return queryForList;
	}

	@Override
	public List<Map<String, Object>> query5(String e1,String e2) {
		List<Map<String, Object>> queryForList = jdbc.queryForList(SqlString.query5,e1,e2);
		return queryForList;
	}

	@Override
	public List<Map<String, Object>> query6(String e1,String e2,String e3) {
		List<Map<String, Object>> queryForList = jdbc.queryForList(SqlString.query6,e1,e2,e3);
		return queryForList;
	}


}
