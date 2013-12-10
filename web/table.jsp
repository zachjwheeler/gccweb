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
            
            function getimghtml(filename, player) {
                if(typeof player === 'undefined')
                    player = false
                var onclick = player ? 'onclick="clickcard(\'' + filename + '\')"' : ''
                return '<img ' + onclick + ' class="card" src="PlayingCards/' + 
                        filename + '.png" alt="' + filename + ' "/>'
            }
            
            function clickcard(filename) {
                if(gameobj !== null && gameobj.playerturn === 0) {
                    if(gameobj.phase === 'tricks')
                        doaction('play', filename)
                    else if(gameobj.phase === 'discard' && gameobj.dealer === 0)
                        doaction('discard', filename)
                }
            }
            
            function displaycards(top, cardlist, player) {
                top.innerHTML = ''
                for(var i=0; i < cardlist.length; ++i) {
                    top.innerHTML += getimghtml(cardlist[i].toString(), player)
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
                    
                    if(!pregame() && game.phase !== 'bidding' && game.phase !== 'preround')
                    {
                        document.getElementById('trumpDisp').src = 'PlayingCards/suit_' + game.trump + '.png';
                        document.getElementById('trumpDispPar').style.display = 'inline';
                    }
                    
                    document.getElementById('dealbutton').style.display = game.phase === 'preround' && game.dealer === 0 ? 'inline' : 'none'
                    
                    document.getElementById('readybutton').style.display = game.phase === 'pregame' ? 'inline' : 'none'
                    document.getElementById('readytext').style.display = game.phase === 'ready' ? 'inline' : 'none'
                }
                
                if(diff('trump')) {
                    
                }
                if(diff('players')) {
                    if(oldgame === null || oldgame.players.length !== game.players.length)
                        document.getElementById('playercount').childNodes[0].data = game.players.length
                }
                if(diff('players') || diff('teammate') || diff('bidwinner')) {
                    if(pregame()) {
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
                    } else {
                        for(var i=0; i < 4; ++i)
                        {
                            document.getElementById('otherPlayerName'+i).innerHTML = game.players[i] +
                                (i == game.bidwinner ? ': BIDWINNER ' : '') +
                                (i == game.bidwinner && game.alone ? '(alone)' : '')
                        }
                    }
                }
                if(diff('cards')) {
                    displaycards(document.getElementById('yourhand'),game.cards,true);
                }
                if(diff('ontable')) {
                    
                    
                }
                if(diff('playerturn') || diff('ontable')) {
                    for(var i=0; i < 4; ++i)
                    {
                        document.getElementById('otherPlayerName'+i).style.color = game.playerturn === i ? 'red' : ''
                        document.getElementById('otherPlayerName'+i).style.fontWeight = game.playerturn === i ? 'bold' : ''
                    }
                    if(game.phase === 'bidding')
                    {
                        var tempCards = new Array()
                        for(var i = 0; i < 5; ++i)
                            tempCards.push(new Card())
                        for(var i = 1; i < 4; ++i)
                            displaycards(document.getElementById('otherPlayer' + i),tempCards)
                    }
                    else if(game.phase === 'discard')
                    {
                        var tempCards = new Array()
                        for(var i = 0; i < 5; ++i)
                            tempCards.push(new Card())
                        for(var i = 1; i < 4; ++i)
                        {
                            if(i === game.dealer)
                            {
                                tempCards.push(new Card())
                                displaycards(document.getElementById('otherPlayer' + i),tempCards);
                                tempCards.pop()
                            }
                            else
                            {
                                displaycards(document.getElementById('otherPlayer' + i),tempCards);
                            }
                        }
                    }
                    else if(game.phase === 'tricks')
                    {
                        var skip = game.alone ? game.partner(game.bidwinner) : -1
                        var eqlen = game.ontable.length
                        for(var i=1; i <= game.ontable.length; ++i)
                            if((game.playerturn-i+4)%4 === skip)
                                ++eqlen
                        var startPlayer = (game.playerturn - eqlen + 4) % 4;
                        var tempCards = new Array();
                        var inert = eqlen === 4 ? 1 : 0
                        for(var i = 0; i < 5 - game.ourtricks - game.theirtricks - 1 + inert; ++i)
                            tempCards.push(new Card());
                        for(var i = 0; i < 4; ++i)
                        {
                            var currentPlayer = (startPlayer+i)%4;
                            if(i === eqlen)
                                tempCards.push(new Card());
                            if(currentPlayer !== 0 && (!game.alone || currentPlayer !== game.partner(game.bidwinner)))
                                displaycards(document.getElementById('otherPlayer' + currentPlayer),tempCards)
                        }
                        if(game.alone) {
                            while(tempCards.length < 5)
                                tempCards.push(new Card())
                            var id = game.partner(game.bidwinner)
                            id = id === 0 ? 'yourhand' : 'otherPlayer'+id
                            displaycards(document.getElementById(id),tempCards)
                        }
                    }
                    
                    var b = game.phase === 'bidding' && game.trumpcard.isReal() && game.playerturn === 0
                    document.getElementById('trumpbutton').style.display = b ? 'inline' : 'none'
                    document.getElementById('goalonebutton').style.display = b ? 'inline' : 'none'
                    document.getElementById('passbutton').style.display = 
                            game.phase === 'bidding' && game.playerturn === 0 && 
                            (game.dealer !== 0 || game.trumpcard.isReal()) ? 'inline' : 'none'
                    
                    if(game.phase === 'bidding' && game.trumpcard.isReal())
                    {
                        document.getElementById('middle').style.display = 'block'
                        displaycards( document.getElementById('middle'),[game.trumpcard])
                    }
                    else if(game.phase === 'bidding')
                    {
                        document.getElementById('middle').style.display = 'none'
                        document.getElementById('callTrump').style.display = game.playerturn === 0 ? 'inline' : 'none'
                    }
                    else if(game.phase === 'tricks' || game.phase === 'end')
                    {
                        document.getElementById('middle').style.display = 'block'
                        document.getElementById('callTrump').style.display = 'none'
                        displaycards( document.getElementById('middle'),game.ontable)
                    }
                    else
                    {
                        document.getElementById('callTrump').style.display = 'none'
                        document.getElementById('middle').style.display = 'none'
                    }
                }
                if(diff('dealer')) {
                    
                }
                if(diff('bidwinner')) {
                    
                }
                if(diff('ourscore')) {
                    document.getElementById('YourScorea').style.display = 'block'
                    document.getElementById('YourScoreb').innerHTML = game.ourscore
                }
                if(diff('theirscore')) {
                    document.getElementById('TheirScorea').style.display = 'block'
                    document.getElementById('TheirScoreb').innerHTML = game.theirscore
                }
                if(diff('ourtricks')) {
                    document.getElementById('YourTrickScorea').style.display = 'block'
                    document.getElementById('YourTrickScoreb').innerHTML = game.ourtricks
                }
                if(diff('theirtricks')) {
                    document.getElementById('TheirTrickScorea').style.display = 'block'
                    document.getElementById('TheirTrickScoreb').innerHTML = game.theirtricks
                }
                if(diff('trumpcard')) {
                    if(game.phase === 'bidding' && !game.trumpcard.isReal())
                    {
                        document.getElementById(game.trump).style.display = 'none'
                        document.getElementById(game.trump+'_alone').style.display = 'none'
                    }
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
            .card {
                margin: 5px;
            }
        </style>
    </head>
    <body onload='init()'>
        <form method='POST' action='ctrl'>
            <input type='hidden' name='quit' value='quit'/>
            <input type='submit' value='Quit'/>
        </form>
        <form method='POST' action='ctrl'>
            <input type='hidden' name='debug-stop' value='debug-stop'/>
            <input type='submit' value='Debug Stop'/>
        </form>
        <p># players: <span id='playercount'>0</span></p>
        <p><span id = 'trumpDispPar' style='display:none;'>Trump: <img id = 'trumpDisp' ></img></span></p>
        <p>
        <div id = 'YourTrickScorea' style='display:none;'>Your Trick Score: <span id = 'YourTrickScoreb'></span></div>
        <div id = 'TheirTrickScorea' style='display:none;'>Their Trick Score: <span id = 'TheirTrickScoreb'></span></div>
        <div id = 'YourScorea' style='display:none;'>Your Score: <span id = 'YourScoreb'></span></div>
        <div id = 'TheirScorea' style='display:none;'>Their Score: <span id = 'TheirScoreb'></span></div>
        </p>
        <span id='pregame'>
            <p>Player: <span id='pregame-username'><%= session.getAttribute("username") %></span></p>
            <span id='pregame-no-teammate' class="teammate-select" onclick='setTeammate("")'>No Teammate Request</span>
            <div id='pregame-playerlist'>
                
            </div>
            <button id='readybutton' onclick='doaction("begin")'>Ready!</button>
            <div id='readytext' style='display:none;'>Waiting for other players...</div>
        </span>
        <span id='table' style='display:none;'>
            <div style = 'text-align:center'> Partner Hand: <span id='otherPlayerName2'></span></div>
            <div id = 'otherPlayer2' style = 'text-align:center'></div>
            <table style='width:100%;'><tr>
            <td>
            <div id = 'otherPlayer1'></div>
            </td>
            <td>
            <span style='text-align:center;' id = 'middle'></span>
            </td>
            <td>
            <div style = 'text-align:right' id = 'otherPlayer3'></div>
            </td>
            </tr></table>
            <span> Left Player's Hand: <span id='otherPlayerName1'></span></span>
            <span style = 'float:right'> Right Player's Hand: <span id='otherPlayerName3'></span></span>
            <p>
            <div id = 'yourhand' style = 'text-align:center'></div>
            <div  style = 'text-align:center'> Your Hand: <span id='otherPlayerName0'><%= session.getAttribute("username") %></span></div>
            </p>
            <p>
            <button id='dealbutton' onclick='doaction("deal")'>Deal</button>
            </p>
            <p>
            <button id='trumpbutton' onclick='doaction("declare trump",gameobj.trumpcard.suit)'>Declare Trump</button>
            </p>
            <p>
            <button id='goalonebutton' onclick='doaction("declare trump",gameobj.trumpcard.suit + "s:alone")'>Go it Alone!</button>
            </p>
            <span id = 'callTrump' style = 'display:none'>
            <p>
            <button id = 's_alone' onclick='doaction("declare trump","s:alone")'>spades alone</button>
            <button id = 'h_alone' onclick='doaction("declare trump","h:alone")'>hearts alone</button>
            <button id = 'c_alone' onclick='doaction("declare trump","c:alone")'>clubs alone</button>
            <button id = 'd_alone' onclick='doaction("declare trump","d:alone")'>diamonds alone</button>
            </p>
            <p>
            <button id = 's' onclick='doaction("declare trump","s")'>spades</button>
            <button id = 'h' onclick='doaction("declare trump","h")'>hearts</button>
            <button id = 'c' onclick='doaction("declare trump","c")'>clubs</button>
            <button id = 'd' onclick='doaction("declare trump","d")'>diamonds</button>
            </p>
            </span>
            <p>
            <button id = 'passbutton' onclick='doaction("pass")'>Pass</button>
            </p>
        </span>
    </body>
</html>
