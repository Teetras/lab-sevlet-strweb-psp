<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 26.10.2023
  Time: 14:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script src="webjars/jquery/3.7.1/jquery.min.js"></script>
    <script src="webjars/bootstrap/5.3.2/js/bootstrap.min.js"></script>
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
    <title> Title</title>

    <!-- Bootstrap core CSS -->
    <link href="webjars/bootstrap/5.3.2/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        .footer {
            bottom: 0;
            width: 100%;
            height: 60px;
        }

        .footer .container {
            width: auto;
            max-width: 680px;
            padding: 0 15px;
        }
    </style>

</head>
<body>

<div class="container">
    <nav class="navbar navbar-expand-lg navbar-light" style="background-color: #e3f2fd;">
        <div class="container-fluid">
            <a class="navbar-brand" href="#">
                <img src="https://www.kv.by/sites/default/files/user7743/logo_iba_group.jpg" alt="" width="30"
                     height="24"
                     class="d-inline-block align-text-top">
                IBA
            </a>
            <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
                <div class="navbar-nav">
                    <a class="nav-link" aria-current="page" href="#">Home</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/controller?command=login_page">Login</a>
                    <a class="nav-link" href="${pageContext.servletContext.contextPath}/controller?command=sign_out">Logout</a>
                </div>
            </div>
        </div>
    </nav>
    <H2>Welcome ${name}</H2>

    <table class="table table-hover">
        <thead class="table-dark">
        <tr>
            <th scope="col">#</th>
            <th scope="col">Имя</th>
            <th scope="col">Телефон</th>
            <th scope="col">Email</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach items="${group}" var="person" varStatus="loop">
            <tr>
                <th scope="row">${loop.index + 1}</th>
                <td>${person.name}</td>
                <td> ${person.phone}</td>
                <td> ${person.email}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>


    <p><font color="red">${errorMessage}</font></p>
    <form method="POST" action="${pageContext.servletContext.contextPath}/controller?command=add_new_person"> Новый :
        <div class="row">
            <div class="col-4">
                <div class="input-group mb-3">
                    <span class="input-group-text">name </span>
                    <input name="nname" type="text" class="form-control">
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-4">
                <div class="input-group mb-3">
                    <span class="input-group-text">phone</span>
                    <input name="nphone" type="text" class="form-control">
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-4">
                <div class="input-group mb-3">
                    <span class="input-group-text">email </span>
                    <input name="nemail" type="text" class="form-control">
                </div>
            </div>
        </div>
        <input name="add" type="submit"/>
    </form>
</div>
<footer class="footer">
    <div class="container">
        <p>2021 Все права защищены</p>
    </div>
</footer>
</body>
</html>


