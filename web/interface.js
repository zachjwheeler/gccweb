
function Game() {
    this.trump=''
    this.players = new Array()
    this.cards = new Array()
    this.ontable = new Array()
    this.playerturn=-1
    this.dealer=-1
    this.bidwinner=-1
    this.ourscore=0
    this.theirscore=0
    this.ourtricks=0
    this.theirtricks=0
    
    this.partner = function(idx) {
        return (idx+2)%players.length
    }
}

function Card() {
    this.suit = ''
    this.type = ''
    
    this.toString = function() {
        return this.type + this.suit
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
        var next = new Card()
        next.suit = cardlist.item(i).getElementsByTagName('suit').item(0).childNodes[0].data
        next.type = cardlist.item(i).getElementsByTagName('type').item(0).childNodes[0].data
        ret.push(next)
    }
    return ret
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

/// Not yet implemented
/// Response will be one of "true", "false", and "invalid".
///   "true": the action was completed successfully.
///   "false": the action could not be completed, e.g. not your turn.
///   "invalid": there is no action called by the given name.
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


/// Can leave off actiondata if it's not needed, eg doaction('deal')
function doaction(action, actiondata) {
    if(typeof actiondata === null)
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
