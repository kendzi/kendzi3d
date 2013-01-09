<%@page import="kendzi.kendzi3d.tile.server.dto.RenderStatus"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>    
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Render status</title>
    </head>
    <body>
        <h1>Render status</h1> 
       	<table>
       	  <thead>
       	   	<tr>
       	   	 <th>Time</th>
       	   	 <th>Succes</th>
       	   	 <th>Hit in catche</th>
       	   	 <th>Render time</th>
       	   	 <th>Tile</th>
       	   	</tr>
       	  </thead>
	      <c:forEach var="rs" items="${renderStatusList}">
	        <tr>
	          <td>${rs.date}</td>
	          <td>${rs.succes}</td>
	          <td>${rs.catche}</td>
	          <td>${rs.time}</td>
	          <td>z: ${rs.tile.z}, x: ${rs.tile.x}, y: ${rs.tile.y}</td>
	        </tr>
	      </c:forEach>
    	</table>
    </body>
</html> 
