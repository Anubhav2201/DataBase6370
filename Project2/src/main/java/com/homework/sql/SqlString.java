/**
 * 
 */
package com.homework.sql;

public class SqlString {
	
//	private String emp1No;
//	
//	private String emp2No;

	public static final String query1 = "SELECT dept_name 'deptName'," + 
			"       ((SELECT avg(s.salary)" + 
			"         FROM employees e, salaries s, dept_emp de" + 
			"         WHERE e.emp_no = s.emp_no" + 
			"               AND e.emp_no = de.emp_no" + 
			"               AND e.gender='F'" + 
			"               AND de.dept_no = d.dept_no)/(  SELECT avg(s1.salary)" + 
			"                                                  FROM employees e1, salaries s1, dept_emp de1" + 
			"                                                  WHERE e1.emp_no = s1.emp_no" + 
			"                                                        AND e1.emp_no = de1.emp_no" + 
			"                                                        AND e1.gender='M'" + 
			"                                                        AND de1.dept_no = d.dept_no)) 'ratio'" + 
			"FROM departments d " + 
			"WHERE" + 
			"  ((SELECT avg(s.salary)" + 
			"    FROM employees e, salaries s, dept_emp de" + 
			"    WHERE e.emp_no = s.emp_no" + 
			"          AND e.emp_no = de.emp_no" + 
			"          AND e.gender='F'" + 
			"          AND de.dept_no = d.dept_no)/(SELECT avg(s1.salary)" + 
			"                                             FROM employees e1, salaries s1, dept_emp de1" + 
			"                                             WHERE e1.emp_no = s1.emp_no" + 
			"                                                   AND e1.emp_no = de1.emp_no" + 
			"                                                   AND e1.gender='M'" + 
			"                                                   AND de1.dept_no = d.dept_no)) =" + 
			"(" + 
			"  SELECT MAX((SELECT avg(s.salary)" + 
			"              FROM employees e, salaries s, dept_emp de" + 
			"              WHERE e.emp_no = s.emp_no" + 
			"                    AND e.emp_no = de.emp_no" + 
			"                    AND e.gender = 'F'" + 
			"                    AND de.dept_no = d.dept_no) / (SELECT avg(s1.salary)" + 
			"                                                       FROM employees e1, salaries s1," + 
			"                                                         dept_emp de1" + 
			"                                                       WHERE e1.emp_no = s1.emp_no" + 
			"                                                             AND e1.emp_no = de1.emp_no" + 
			"                                                             AND e1.gender = 'M'" + 
			"                                                             and de1.dept_no = d.dept_no)) 'maxRatio'" + 
			"  from departments d" + 
			")";
	
	public static final String query2 = "select concat(e.first_name,' ',e.last_name) 'full_name'," + 
			"      datediff(if(date_format(to_date, '%Y-%m-%d')='9999-01-01',date(sysdate()),to_date),dm.from_Date) 'days'" + 
			"		FROM DEPT_MANAGER dm, EMPLOYEES e" + 
			"		WHERE dm.emp_no = e.emp_no" + 
			" 		AND datediff(if(date_format(to_date, '%Y-%m-%d')='9999-01-01',date(sysdate()),to_date),dm.from_Date) =" + 
			"		(" + 
			"      select MAX(" + 
			"          datediff(if(date_format(to_date, '%Y-%m-%d') = '9999-01-01', date(sysdate()), to_date), dm.from_Date)" + 
			"      ) 'maxDays'" + 
			"		FROM DEPT_MANAGER DM, EMPLOYEES E" + 
			"		WHERE DM.emp_no = e.emp_no" + 
			")";
	
	public static final String query3 = "SELECT" + 
			"	d.dept_name," + 
			"  concat(substring(date_format(e.birth_date, '%Y'), 1, 3), '0') 'years'," + 
			"  count(*) 'empcount'," + 
			"  round(avg(s.salary), 2)  'avgSalary' " + 
			"FROM DEPT_EMP de," + 
			"  employees e," + 
			"  salaries s," + 
			"	departments d " + 
			"WHERE" + 
			"  DE.emp_no = E.emp_no " + 
			"  and e.emp_no = s.emp_no " + 
			"	and de.dept_no = d.dept_no " + 
			"GROUP BY d.dept_name," + 
			"  concat(substring(date_format(e.birth_date, '%Y'), 1, 3), '0')";
	
	public static final String query4 = "SELECT " + 
			"  concat(e.first_name, ' ', e.last_name) 'full_name'  " + 
			"FROM employees e, " + 
			"  salaries s " + 
			"WHERE e.emp_no = S.emp_no " + 
			"      AND s.salary > 80000 / 12  " + 
			"      AND e.gender = 'F' " + 
			"      AND date_format(e.birth_date, '%Y-%m-%d') < '1990-01-01' " + 
			"      AND EXISTS(SELECT * " + 
			"                 FROM DEPT_MANAGER dm_exists " + 
			"                 WHERE dm_exists.emp_no = E.emp_no)";
	
	public static final String query5 = "select dept_no from dept_emp where emp_no = ? and dept_no in ( " + 
			"select dept_no from dept_emp where emp_no = ?)";
	
	public static final String query6 = "select dept_no from dept_emp where emp_no = ? and dept_no in(" + 
			"select dept_no from dept_emp where emp_no = ? and dept_no in (" + 
			"select dept_no from dept_emp where emp_no = ?)";
}
