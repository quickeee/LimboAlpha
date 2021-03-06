<%@ page language="java" contentType="text/html; charset=utf8"
	pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf8">
	<title><spring:message code="label.title" /></title>
</head>
<body>
<jsp:include page="main.jsp" />
<h2><spring:message code="label.title" /></h2>

<form:form method="post" action="add" commandName="client">

	<table>
		<tr>
			<td><form:label path="name">
				<spring:message code="label.name" />
			</form:label></td>
			<td><form:input path="name" /></td>
		</tr>
		<tr>
			<td><form:label path="pvnNumber">
				<spring:message code="label.pvnNumber" />
			</form:label></td>
			<td><form:input path="pvnNumber" /></td>
		</tr>
		<tr>
			<td><form:label path="country">
				<spring:message code="label.country" />
			</form:label></td>
			<td><form:input path="country" /></td>
		</tr>
		<tr>
			<td><form:label path="city">
				<spring:message code="label.city" />
			</form:label></td>
			<td><form:input path="city" /></td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit"
				value="<spring:message code="label.addclient"/>" /></td>
		</tr>
	</table>
</form:form>

<h3><spring:message code="label.clients" /></h3>
<c:if test="${!empty clientList}">
	<table class="data">
		<tr>
			<th><spring:message code="label.name" /></th>
			<th><spring:message code="label.pvnNumber" /></th>
			<th><spring:message code="label.country" /></th>
			<th>&nbsp;</th>
		</tr>
		<c:forEach items="${clientList}" var="client" varStatus="status">
			<tr>
				<td><input name="contacts[${status.index}].firstname" value="${client.name}"/></td>
				<td><input name="contacts[${status.index}].firstname" value="${client.pvnNumber}"/></td>
				<td><input name="contacts[${status.index}].firstname" value="${client.country}"/></td>
				<td><a href="delete/${client.id}"><spring:message code="label.delete" /></a></td>
			</tr>
		</c:forEach>
	</table>
</c:if>

</body>
</html>