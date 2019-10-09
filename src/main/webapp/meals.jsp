<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<jsp:useBean id="service" scope="request" class="ru.javawebinar.topjava.service.MealServiceImpl" />

<c:set var="meals" scope="request" value="${service.findAll()}" />

<c:set var="datetimeFormatter" scope="request" value="${DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm')}" />

<html>
<head>
    <title>Meals</title>
</head>
<body>
    <h3><a href="index.html">Home</a></h3>
    <hr>
    <h2>Meals list</h2>
    <table>
        <thead>
            <tr>
                <th>Дата-время</th>
                <th>Блюдо</th>
                <th>Калории</th>
                <th>Превышение</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${meals}" var="meal">
                <tr>
                    <td>${meal.getDateTime().format(datetimeFormatter)}</td>
                    <td>${meal.getDescription()}</td>
                    <td>${meal.getCalories()} </td>
                    <td>${meal.isExcess()}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>