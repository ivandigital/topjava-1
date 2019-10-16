<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<%--<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>--%>
<html>
<head>
    <title>Meal list</title>
    <style>
        .normal {
            color: green;
        }

        .excess {
            color: red;
        }
    </style>
</head>
<body>
<section>
    <h3><a href="index.html">Home</a></h3>
    <hr/>
    <h2>Meals</h2>
    <form method="get" action="meals">
        <table id="filter">
            <tbody>
                <tr>
                    <td>Date interval</td>
                    <td>
                        <input type="date" name="dateBegin" value="${param.dateBegin}" />
                    </td>
                    <td>
                        <input type="date" name="dateEnd" value="${param.dateEnd}" />
                    </td>
                </tr>
                <tr>
                    <td>Time interval</td>
                    <td>
                        <input type="time" name="timeBegin" value="${param.timeBegin}" />
                    </td>
                    <td>
                        <input type="time" name="timeEnd" value="${param.timeEnd}" />
                    </td>
                </tr>
                <tr>
                    <td colspan="3" align="right">
                        <input type="submit" id="filterApply" value="Filter" />
                        <input type="reset" id="filterReset" value="Reset"/>
                    </td>
                </tr>
            </tbody>
        </table>
    </form>
    <a href="meals?action=create">Add Meal</a>
    <br><br>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th></th>
        </tr>
        </thead>
        <c:forEach items="${meals}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
            <tr class="${meal.excess ? 'excess' : 'normal'}">
                <td>
                        <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                        <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                        <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                        ${fn:formatDateTime(meal.dateTime)}
                </td>
                <td><a href="meals?action=update&id=${meal.id}">${meal.description}</a></td>
                <td>${meal.calories}</td>
                <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>