<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <script src="https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
    <title>Employees</title>
</head>
<body>

<input type="button" value="query1" onclick="show('d1')"> </input>
<div id="d1" hidden>
    Department(s) with maximum ratio of average female salaries to average men salaries are: ${query1.deptName}，ratio is#{query1.ratio}。</br>
</div>
</br>

<input type="button" value="query2" onclick="show('d2')"> </input>
<div id="d2" hidden>
    Manager(s) who holds office for the longest duration are：</br>
<#list query2 as manager>
  Full name：${manager.full_name} &nbsp;
  Number of days in office：${manager.days}
  </br>
</#list>
</div>
</br>

<input type="button" value="query3" onclick="show('d3')"> </input>
<div id="d3" hidden>
    Number of employees born in each decade  and their average salaries：</br>
	<table border="1">
		<tr>
			<td>Department Name</td>
			<td>Year</td>
			<td>Number of employees</td>
			<td>Average salaries</td>
		</tr>
<#list query3 as emp>
  		<tr>
			<td>${emp.dept_name}</td>
			<td>${emp.years}</td>
			<td>${emp.empcount}</td>
			<td>${emp.avgSalary}</td>
		</tr>
</#list>
	</table>
</div>
</br>

<input type="button" value="query4" onclick="show('d4')"> </input>
<div id="d4" hidden>
    Female managers born before January 1, 1990, with an annual salary of more than 80K, are
<#list query4 as manager>
	${manager.full_name} &nbsp;&nbsp;
</#list>
</div>
</br>
E1 id：<input type="text" id="t1"/></br>
E2 id：<input type="text" id="t2"/></br>
E3 id：<input type="text" id="t3"/></br>
<input type="button" value="query5" onclick="check(1)"/>
<input type="button" value="query6" onclick="check(2)"/>
<script>

	function show(id){
		var doc = document.getElementById(id);
		doc.removeAttribute("hidden");
	}
	
	function check(type){
		var e1 = document.getElementById("t1").value;
		var e2 = document.getElementById("t2").value;
		var e3 = document.getElementById("t3").value;
		var url = "/separate?type="+type+"&e1="+e1+"&e2="+e2+"&e3="+e3;
		$.ajax({url:url,success:function(result){
			alert(result);
		}});
	}
	

</script>

</body>
</html>