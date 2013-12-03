<%
    if(session == null || session.getAttribute("username") == null){
        response.sendRedirect("index.jsp");
    }
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Table</title>
        <script src='interface.js'></script>
        <script type='text/javascript'>
            function init() {
                oldgame = new Game();
                game = new Game();
                
                update();
                setInterval(update, 1000)
            }
            function update() {
                oldgame = game
                ajaxupdategame(function(obj) {
                    game = obj
                    updatehtml()
                });
            }
            function updatehtml() {
                if(diff('trump')) {
                    
                }
                if(diff('players')) {
                    if(oldgame.players.length != game.players.length)
                        document.getElementById('playercount').childNodes[0].data = game.players.length
                }
                if(diff('cards')) {
                    
                }
                if(diff('ontable')) {
                    
                }
                if(diff('playerturn')) {
                    
                }
                if(diff('dealer')) {
                    
                }
                if(diff('bidwinner')) {
                    
                }
                if(diff('ourscore')) {
                    
                }
                if(diff('theirscore')) {
                    
                }
                if(diff('ourtricks')) {
                    
                }
                if(diff('theirtricks')) {
                    
                }
            }
            function diff(v) {
                return oldgame[v].toString() !== game[v].toString()
            }
        </script>
    </head>
    <body onload='init()'>
        <p># players: <span id='playercount'>0</span></p>
        <p>Player: <span id='username'><%= session.getAttribute("username") %></span></p>
    </body>
</html>
