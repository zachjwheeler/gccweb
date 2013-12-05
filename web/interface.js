
function Game() {
    this.trump=''
    this.players = new Array()
    this.cards = new Array()
    this.ontable = new Array()
    this.playerturn=-1         // left-of-dealer = 1
    this.dealer=-1
    this.bidwinner=-1
    this.ourscore=0
    this.theirscore=0
    this.ourtricks=0
    this.theirtricks=0
    this.trumpcard = new Card()
    this.phase = 'pregame'   // options: 'pregame', 'ready', 'preround', 'bidding', 'tricks', 'end'
    this.teammate = ''       // the requested or present teammate's username
    
    this.partner = function(idx) {
        return (idx+2)%players.length
    }
    
    // Assuming the game's in bidding phase
    this.isFirstBiddingRound = function() {
        return this.trumpcard.isReal()
    }
}

function Card(s, t) {
    this.suit = typeof s === 'undefined' ? '' : s
    this.type = typeof t === 'undefined' ? '' : t
    
    this.toString = function() {
        return this.isReal() ? this.type + this.suit : 'back'
    }
    this.isReal = function() {
        return this.type !== '' && this.type !== ''
    }
}

function ajaxparseobj(doc) {
    var game = new Game()
    game.trump = doc.getElementsByTagName('trump').item(0).childNodes[0].data
    if(game.trump === 'none')
        game.trump = ''
    game.cards = ajaxgetcards(doc.getElementsByTagName('cards').item(0))
    game.ontable = ajaxgetcards(doc.getElementsByTagName('ontable').item(0))
    var playerlist = doc.getElementsByTagName('player')
    for(var i=0; i < playerlist.length; ++i) {
        game.players.push(playerlist.item(0).childNodes[0].data)
    }
    game.trumpcard = ajaxgetcards(doc.getElementsByTagName('trumpcard').item(0))
    if(game.trumpcard.length > 0)
        game.trumpcard = game.trumpcard[0]
    else
        game.trumpcard = new Card()
    game.teammate = doc.getElementsByTagName('teammate').item(0).childNodes[0]
    if(typeof game.teammate === 'undefined')
        game.teammate = ''
    else
        game.teammate = game.teammate.data
    game.phase = doc.getElementsByTagName('phase').item(0).childNodes[0].data
    game.playerturn = parseInt(doc.getElementsByTagName('playerturn').item(0).childNodes[0].data)
    game.dealer = parseInt(doc.getElementsByTagName('dealer').item(0).childNodes[0].data)
    game.bidwinner = parseInt(doc.getElementsByTagName('bidwinner').item(0).childNodes[0].data)
    game.ourscore = parseInt(doc.getElementsByTagName('ourscore').item(0).childNodes[0].data)
    game.theirscore = parseInt(doc.getElementsByTagName('theirscore').item(0).childNodes[0].data)
    game.ourtricks = parseInt(doc.getElementsByTagName('ourtricks').item(0).childNodes[0].data)
    game.theirtricks = parseInt(doc.getElementsByTagName('theirtricks').item(0).childNodes[0].data)
    return game
}

function ajaxgetcards(top) {
    var cardlist = top.getElementsByTagName('card')
    var ret = new Array()
    for(var i=0; i < cardlist.length; ++i) {
        ret.push(getcard(cardlist.item(i)))
    }
    return ret
}

function getcard(top) {
    var next = new Card()
    next.suit = top.getElementsByTagName('suit').item(0).childNodes[0].data
    next.type = top.getElementsByTagName('type').item(0).childNodes[0].data
    return next
}

function ajaxupdategame(callback) {
    var req = new XMLHttpRequest()
    if(req) {
        req.open("POST", "ctrl", true)
        req.onreadystatechange = function() {
            if(req.readyState === 4 && req.status === 200)
                callback(ajaxparseobj(req.responseXML.documentElement))
        }
        req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
        req.setRequestHeader('Ajax', 'get')
        req.setRequestHeader('resource', 'game')
        req.send('')
    }
}

function ajaxget(resource, callback) {    
    var req = new XMLHttpRequest()
    if(req) {
        req.open("POST", "ctrl", true)
        req.readystateonchange = function() {
            if(req.readyState === 4 && req.status === 200)
                callback(req.responseXML.documentElement.childNodes[0].data)
        }
        req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
        req.setRequestHeader('Ajax', 'get')
        req.setRequestHeader('resource', resource)
        req.send('')
    }
}

/// Response will be one of "true", "false", "invalid action", "invalid data", and "crash".
///   "true": the action was completed successfully.
///   "false": the action could not be completed, e.g. not your turn.
///   "invalid action": there is no action called by the given name.
///   "invalid data": data string was not in an acceptable format
///   "crash": there's a bug server-side
/// If you get "false" or "invalid", I would expect that either there's a bug
//   or the user is trying to hack the system.
/// Check for all three to make debugging easier, but if the client code is
///  correct the response should almost always be "true".
function ajaxaction(action, actiondata, callback) {
    var req = new XMLHttpRequest()
    if(req) {
        req.open('POST', 'ctrl', true)
        req.onreadystatechange = function() {
            if(req.readyState === 4 && req.status === 200)
                callback(req.responseXML.documentElement.childNodes[0].data)
        }
        req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
        req.setRequestHeader('Ajax', 'set')
        req.setRequestHeader('action', action)
        req.setRequestHeader('actiondata', actiondata)
        req.send('')
    }
}


// Can leave off actiondata if it's not needed, eg doaction('deal')
/***** List of actions ******
 * 'deal': shuffle & deal cards to everyone to start a round
 * 'play', card: play the card given by 'card', eg doaction('play', '2c') for
 *             the 2 of clubs. The toString() method in Card will give you '2c', etc.
 * 'declare trump', trump: declare trump, 'trump' is ignored if it's the first
 *                        round of bidding.
 * 'pass': used during bidding rounds
 * 'begin': player considers himself ready to begin the game, valid during 'pregame' phase
 * 'request teammate', teammate: valid during 'pregame' phase, sending this multiple times
 *                               is supported
 */
function doaction(action, actiondata) {
    if(typeof actiondata === 'undefined')
        actiondata = ''
    ajaxaction(action, actiondata, 
        function(result) {
            if(result !== 'true') {
                alert('Either we\'ve experienced a bug, or you\'re trying to hack the game.')
            } else {
                update()
            }
        }
    );
}

gameobj = null
function update() {
    ajaxupdategame(function(obj) {
        updatehtml(gameobj, obj)
        gameobj = obj
    })
}
