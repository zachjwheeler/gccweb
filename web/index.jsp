<%
    if(session != null && session.getAttribute("username") != null){
        response.sendRedirect("table.jsp");
    }
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Euchre Login</title>
    </head>
    <body>
        <h1>Let's play Euchre!</h1>
        <%
        if(session != null && session.getAttribute("invalidUsername") != null) {
            out.println("<p style='color:red;'>The name `" + 
                    session.getAttribute("invalidUsername") + "' is "
                    + "in use, but that's the wrong user code.");
        }
        if(session != null && session.getAttribute("full") != null) {
            out.println("<p style='color:red;'>The game is already full.");
        }
        %>
        <form method="POST" action="ctrl">
            <table>
                <tr>
                    <td><label for="username">Display Name:</label></td>
                    <td><input type="text" id="username" name="username"/></td>
                </tr>
                <tr>
                    <td><label for="password">User Code:</label></td>
                    <td><input type="password" id="password" name="password"/></td>
                </tr>
            </table>
            <p>You will need your user code to log back in if you leave the game page.</p>
            <input type="submit" value="Join Game"/>
        </form>
    </body>
</html>
