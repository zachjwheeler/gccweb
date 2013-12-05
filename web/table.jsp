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
                update();
                setInterval(update, 1000)
            }
            
            function getimghtml(filename) {
                return '<img class="card" src="PlayingCards/' + filename + '.png" alt="' + filename + ' "/>'
            }
            
            function displaycards(top, cardlist, onedown) {
                top.innerHTML = ''
                for(var i=0; i < cardlist.length; ++i) {
                    top.innerHTML += getimghtml(cardlist[i].type + cardlist[i].suit)
                }
                if(onedown) {
                    top.innerHTML += getimghtml('back')
                }
            }
            
            function setdisabled(id, yes) {
                var b = document.getElementById(id)
                if(b)
                    b.disabled = yes
            }
            
            function updatehtml(oldgame, game) {
                function diff(v) {
                    return oldgame === null || oldgame[v].toString() !== game[v].toString()
                }
                
                if(diff('trump')) {
                    
                }
                if(diff('players')) {
                    if(oldgame === null || oldgame.players.length !== game.players.length)
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
                if(diff('trumpcard')) {
                    
                }
                if(diff('phase')) {
                    
                }
                if(diff('teammate')) {
                    
                }
            }
        </script>
    </head>
    <body onload='init()'>
        <p># players: <span id='playercount'>0</span></p>
        <p>Player: <span id='username'><%= session.getAttribute("username") %></span></p>
    </body>
</html>
