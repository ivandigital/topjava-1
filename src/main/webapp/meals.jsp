<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<c:set var="datetimeFormatter" scope="request" value="${DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm')}" />

<html>
<head>
    <title>Meals</title>
    <link rel="stylesheet" type="text/css" href="styles.css" />
</head>
<body>
    <h3><a href="index.html">Home</a></h3>
    <hr>
    <h2>Список приемов пищи</h2>
    <table class="tableList">
        <thead>
            <tr>
                <th>Дата-время</th>
                <th>Прием пищи</th>
                <th>Калории</th>
                <th class="hidden">Превышение</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${meals}" var="meal">
              <c:if test="${meal.isExcess()}">
                <tr class="over-calories">
              </c:if>
              <c:if test="! ${meal.isExcess()}">
                <tr>
              </c:if>
                    <td>
                        ${meal.getDateTime().format(datetimeFormatter)}
                    </td>
                    <td>
                        <a href="meals?id=${meal.getId()}">${meal.getDescription()}</a>
                    </td>
                    <td class="number">
                        ${meal.getCalories()}
                    </td>
                    <td class="hidden">
                        ${meal.isExcess()}
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>