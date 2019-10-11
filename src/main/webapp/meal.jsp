<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<jsp:useBean id="service" scope="request" class="ru.javawebinar.topjava.service.MealServiceImpl" />

<c:set var="datetimeFormatter" scope="request" value="${DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm')}" />

<html>
<head>
    <title>Meal #${id}</title>
    <link rel="stylesheet" type="text/css" href="styles.css" />
</head>
<body>
    <h3><a href="index.html">Home</a></h3>
    <hr>
<c:if test="${meal == null}">
    <h2>Нет данных (Id=${id})</h2>
</c:if>

<c:if test="${meal != null}">
    <h2>Прием пищи #${id}</h2>

    <table class="tableCard">
        <tbody>
            <tr>
                <td>Id</td>
                <td>${meal.getId()}</td>
            </tr>
            <tr>
                <td>Дата-время</td>
                <td>${meal.getDateTime().format(datetimeFormatter)}</td>
            </tr>
            <tr>
                <td>Прием пищи</td>
                <td>${meal.getDescription()}</td>
            </tr>
            <tr>
                <td>Калории</td>
                <td>${meal.getCalories()}</td>
            </tr>
            <tr>
                <td>Превышение</td>
                <td>${meal.isExcess()}</td>
            </tr>
        </tbody>
    </table>
</c:if>
</body>
</html>