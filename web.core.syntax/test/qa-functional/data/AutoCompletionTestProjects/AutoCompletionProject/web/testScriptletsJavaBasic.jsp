<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Scriptlets Code Completion Page</title>
    </head>
    <body>

    <h1>JSP Scriptlets Code Completion Page</h1>

<%-- Java completion for TestBean class --%>
<%--
<% TESTBe|
...
<% TESTBean
--%>

<% TESTBean tBean; %>

<%-- Java completion for variables between more sriptlets --%>
<%--CC
<% tB|
...
<% tBean
--%>

<%-- Java completion for variables methods and fields between more sriptlets --%>
<%--
<% tBean.setN|
...
<% tBean.setName(name)
--%>

<%-- completion for String object inside scriptlets --%>
<%--CC
<% "Hello World !".o|
...
<% "Hello World !".offsetByCodePoints(index, codePointOffset)
--%>

    </body>
</html>
