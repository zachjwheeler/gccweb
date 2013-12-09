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
            
            function displaycards(top, cardlist) {
                top.innerHTML = ''
                for(var i=0; i < cardlist.length; ++i) {
                    top.innerHTML += getimghtml(cardlist[i].type + cardlist[i].suit)
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
                function pregame() {
                    return game.phase === 'pregame' || game.phase === 'ready'
                }
                
                if(diff('phase')) {
                    oldgame = null
                    document.getElementById('pregame').style.display = pregame() ? 'inline' : 'none'
                    document.getElementById('table').style.display = !pregame() ? 'inline' : 'none'
                    
                    document.getElementById('readybutton').style.display = game.phase === 'pregame' ? 'inline' : 'none'
                    document.getElementById('readytext').style.display = game.phase === 'ready' ? 'inline' : 'none'
                }
                
                if(diff('trump')) {
                    
                }
                if(diff('players')) {
                    if(oldgame === null || oldgame.players.length !== game.players.length)
                        document.getElementById('playercount').childNodes[0].data = game.players.length
                }
                if(diff('players') || diff('teammate')) {
                    var top = document.getElementById('pregame-playerlist')
                    var str = ''
                    if(game.players.length > 1)
                        str += '<div>Other Players:</div><ul>'
                    for(var i=1; i < game.players.length; ++i) {
                        var color = game.teammate === game.players[i] ? 'color:green;' : ''
                        var cursor = game.phase === 'pregame' ? 'cursor:pointer;' : ''
                        str += '<li class="teammate-select" style="' + color + cursor +
                                '" onclick="setTeammate(\'' + game.players[i] + '\');">' + 
                                game.players[i] + '</li>'
                    }
                    if(game.players.length > 1)
                        str += '</ul>'
                    top.innerHTML = str
                    document.getElementById('pregame-no-teammate').style.cursor = game.phase === 'pregame' ? 'pointer' : 'auto'
                    document.getElementById('pregame-no-teammate').style.color = 
                            (game.teammate === null || game.teammate === '') ? 'green' : ''
                }
                if(diff('cards')) {
                    displaycards(document.getElementById('yourhand'),game.cards);
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
                if(diff('teammate')) {
                    
                }
            }
            
            function setTeammate(teammate) {
                if(gameobj.phase === 'pregame')
                    doaction("request teammate", teammate)
            }
        </script>
        <style type='text/css'>
            .teammate-select {
                text-decoration:underline;
                color:blue;
            }
        </style>
    </head>
    <body onload='init()'>
        <form method='POST' action='ctrl'>
            <input type='hidden' name='quit' value='quit'/>
            <input type='submit' value='Quit'/>
        </form>
        <p># players: <span id='playercount'>0</span></p>
        <p>Player: <span id='username'><%= session.getAttribute("username") %></span></p>
        <span id='pregame'>
            <span id='pregame-no-teammate' class="teammate-select" onclick='setTeammate("")'>No Teammate Request</span>
            <div id='pregame-playerlist'>
                
            </div>
            <button id='readybutton' onclick='doaction("begin")'>Ready!</button>
            <div id='readytext' style='display:none;'>Waiting for other players...</div>
        </span>
        <span id='table' style='display:none;'>
            Table
            <div id = 'partnerhand'>test</div>
            1
            <div id = 'lefthand'></div>
            2
            <div id = 'righthand'></div>
            3
            <div id = 'yourhand'></div>
            4
        </span>
    </body>
</html>
