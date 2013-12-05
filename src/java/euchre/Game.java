
package euchre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Game {
    private Map<String, Player> players;
    private ArrayList<String> playerOrder;
    private ArrayList<Card> onTable;
    private int playerTurn, dealer;
    private String trump;
    private int bidWinner;
    private Card trumpCard;
    private boolean alone;
    private Card[] deck;
    private int deckposn;
    
    public final static String[] suits = new String[]{"c","d","h","s"};
    public final static String[] types = new String[]{"a","k","q","j","10","9"};
    
    public Game() {
        players = new HashMap<String, Player>();
        playerOrder = new ArrayList<String>();
        onTable = new ArrayList<Card>();
        clearVars();
        deck = new Card[52];
        int idx=0;
        for(String suit : suits) {
            for(String type : types) {
                deck[idx++] = new Card(suit, type);
            }
        }
    }
    
    private void clearVars() {
        playerTurn = dealer = bidWinner = -1;
        trump = "";
        trumpCard = null;
        deckposn=0;
        alone = false;
    }
    
    public void shuffle() {
        deckposn=0;
        Collections.shuffle(Arrays.asList(deck));
    }
    
    public Object getResource(String res) {
        if(res.equals("playercount")) return players.size();
        
        return "";
    }
    
    public boolean inProgress() {
        return players.size() == 4;
    }
    
    private void startGame() {
        Collections.shuffle(playerOrder);
        shuffle();
    }
    private void endGame() {
        players.clear();
        playerOrder.clear();
        onTable.clear();
        clearVars();
    }
    
    public boolean tryAddPlayer(String username, String password) {
        if(players.containsKey(username))
            return players.get(username).getPassword().equals(password);
        if(players.size() == 4)
            return false;
        players.put(username, new Player(password));
        playerOrder.add(username);
        if(players.size() == 4)
            startGame();
        return true;
    }
    
    private int makeRelative(int v, int me) {
        return v >= 0 ? (me - v + players.size()) % players.size() : -1;
    }
    
    public GameBean getBean(String username) {
        if(username == null || !players.containsKey(username)) return null;
        Player player = players.get(username);
        GameBean bean = new GameBean();
        int me = playerOrder.indexOf(username);
        ArrayList<String> p = (ArrayList<String>)playerOrder.clone();
        Collections.rotate(p, me);
        bean.setPlayers(p);
        bean.setPlayerTurn(makeRelative(playerTurn, me));
        bean.setDealer(makeRelative(dealer, me));
        bean.setCards(player.getCards());
        bean.setTrump(trump.equals("") ? "none" : trump);
        bean.setTrumpCard(trumpCard);
        bean.setOnTable(onTable);
        bean.setBidWinner(makeRelative(bidWinner, me));
        bean.setOurScore(player.getOurScore());
        bean.setOurTricks(player.getOurTricks());
        Player opponent = players.get(playerOrder.get((me+1)%playerOrder.size()));
        bean.setTheirScore(opponent.getOurScore());
        bean.setTheirTricks(opponent.getOurTricks());
        bean.setTeammate(player.getTeammate());
        bean.setPhase(player.getPhase());
        bean.setAlone(alone);
        return bean;
    }

    public String tryAction(String username, String action, String actiondata) {
        if(username == null || username.equals(""))
            return "invalid";
        
        Action a = actionMap.get(action);
        if(a == null)
            return "invalid action";
        else
            return a.go(username, actiondata);
    }
    
    private Card draw() {
        return deck[deckposn++];
    }
    
    private interface Action {
        public String go(String username, String data);
    }
    
    private void setPhaseAll(String phase) {
        for(Player pp : players.values())
            pp.setPhase(phase);
    }
    private int next(int v) {
        v = (v+1)%players.size();
        if(alone && partner(bidWinner) == v)
            return (v+1)%players.size();
        return v;
    }
    private int partner(int v) {
        return (v+2)%players.size();
    }
    
    private static final String[] trumptypeplace = new String[]{"j","a","k","q","10","9"};
    private static final String[] typeplace = new String[]{"a","k","q","j","10","9"};
    private boolean cardLess(Card a, Card b) {
        if(sameSuit(a,b)) {
            if(isTrump(a)) {
                if(a.type.equals(b.type)) {
                    if(a.type.equals("j") && !a.suit.equals(b.suit))
                        return b.suit.equals(trump);
                } else {
                    return Arrays.asList(trumptypeplace).indexOf(a.type) > 
                            Arrays.asList(trumptypeplace).indexOf(b.type);
                }
            } else {
                return Arrays.asList(typeplace).indexOf(a.type) >
                        Arrays.asList(typeplace).indexOf(b.type);
            }
            return false;
        }
        // a & b different suits
        if(!onTable.isEmpty() && sameSuit(a, onTable.get(0)))
            return isTrump(b);
        if(!onTable.isEmpty() && sameSuit(b, onTable.get(0)))
            return !isTrump(a);
        return false;
    }
    private boolean isTrump(Card card) {
        return card.suit.equals(trump) || card.equals(leftBower());
    }
    private boolean sameSuit(Card a, Card b) {
        boolean ta = isTrump(a), tb = isTrump(b);
        return (ta && tb) || (!ta && !tb && a.suit.equals(b.suit));
    }
    private Card leftBower() {
        Card r = new Card(null, "j");
        if(trump.equals("s"))
            r.suit = "c";
        else if(trump.equals("c"))
            r.suit = "c";
        else if(trump.equals("d"))
            r.suit = "h";
        else if(trump.equals("h"))
            r.suit = "d";
        else
            throw new RuntimeException();
        return r;
    }
    private boolean hasSuit(ArrayList<Card> cards, Card same) {
        for(Card card : cards) {
            if(sameSuit(card, same))
                return true;
        }
        return false;
    }
    private boolean tableFull() {
        return (alone && onTable.size() == 3) || onTable.size() == 4;
    }
    
    private int winTable() {
        int lead = next(playerTurn);
        int idx=0;
        for(int i=1; i < onTable.size(); ++i) {
            Card top = onTable.get(idx);
            Card next = onTable.get(i);
            if(cardLess(top, next)) {
                idx = i;
            }
        }
        return (idx + lead) % players.size();
    }
    
    private void addTrick(int v) {
        Player a = players.get(playerOrder.get(v)), b = players.get(playerOrder.get(partner(v)));
        a.addTrick();
        b.addTrick();
    }
    
    private Map<String, Action> actionMap = new HashMap<String, Action>() {{
        put("deal", new Action() {
            public String go(String username, String data) {
                Player p = players.get(username);
                if(p.getPhase().equals("preround") && dealer == playerOrder.indexOf(username)) {
                    shuffle();
                    for(Player pp : players.values())
                        pp.clear();
                    for(int i=0; i<5; ++i) {
                        for(String pu : playerOrder) {
                            Player pp = players.get(pu);
                            pp.addCard(draw());
                        }
                    }
                    trumpCard = draw();
                    setPhaseAll("bidding");
                    playerTurn = next(dealer);
                    onTable.clear();
                    alone = false;
                    return "true";
                }
                return "false";
            }
        });
        put("play", new Action() {
            public String go(String username, String data) {
                Player p = players.get(username);
                if(p.getPhase().equals("tricks") && playerOrder.indexOf(username) == playerTurn) {
                    Card card = Card.fromString(data);
                    if(card == null)
                        return "invalid data";
                    if(p.hasCard(card) && (onTable.isEmpty() || tableFull() ||
                            sameSuit(card, onTable.get(0)) ||
                            !hasSuit(p.getCards(), onTable.get(0)))) {
                        if(tableFull())
                            onTable.clear();
                        onTable.add(p.removeCard(card));
                        if(tableFull()) {
                            int winner = winTable();
                            addTrick(winner);
                            if(p.cardCount() == 0) {
                                //TODO: update scores, change phase
                            }
                            playerTurn = winner;
                        } else {
                            playerTurn = next(playerTurn);
                        }
                        return "true";
                    }
                }
                return "false";
            }
        });
        put("declare trump", new Action() {
            public String go(String username, String data) {
                Player p = players.get(username);
                if(p.getPhase().equals("bidding") && playerTurn == playerOrder.indexOf(username)) {
                    StringTokenizer tokenizer = new StringTokenizer(data, ":");
                    int cnt=0;
                    String suit=null, alonestr=null;
                    while(tokenizer.hasMoreTokens() && cnt < 2) {
                        String x = tokenizer.nextToken();
                        if(cnt==0)
                            suit = x;
                        else if(cnt==1)
                            alonestr = x;
                        ++cnt;
                    }
                    if(alonestr != null && !alonestr.equals("alone"))
                        return "invalid data";
                    if(trumpCard == null) {
                        if(!Arrays.asList(suits).contains(suit))
                            return "invalid data";
                        trump = data;
                        setPhaseAll("tricks");
                    } else {
                        trump = trumpCard.suit;
                        players.get(playerOrder.get(dealer)).addCard(trumpCard);
                        trumpCard = null;
                        setPhaseAll("discard");
                    }
                    alone = alonestr != null;
                    bidWinner = playerTurn;
                    playerTurn = next(dealer);
                    return "true";
                }
                return "false";
            }
        });
        put("pass", new Action() {
            public String go(String username, String data) {
                Player p = players.get(username);
                // can't pass if it's the second round of bidding & you're the dealer
                if(p.getPhase().equals("bidding") && playerTurn == playerOrder.indexOf(username) &&
                        (trumpCard != null || playerTurn != dealer)) {
                    if(playerTurn == dealer)
                        trumpCard = null;
                    playerTurn = next(playerTurn);
                    return "true";
                }
                return "false";
            }
        });
        put("begin", new Action() {
            public String go(String username, String data) {
                Player p = players.get(username);
                if(p.getPhase().equals("pregame")) {
                    p.setPhase("ready");
                    if(players.size() == 4) {
                        boolean move=true;
                        for(Player pp : players.values())
                            if(!pp.getPhase().equals("ready"))
                                move=false;
                        if(move)
                            setPhaseAll("preround");
                    }
                    return "true";
                }
                return "false";
            }
        });
        put("request teammate", new Action() {
            public String go(String username, String data) {
                Player p = players.get(username);
                if(p.getPhase().equals("pregame")) {
                    if(data == null || data.equals("") || 
                            (!data.equals(username) && players.containsKey(data))) {
                        p.setTeammate(data == null ? "" : data);
                        return "true";
                    }
                }
                return "false";
            }
        });
        put("discard", new Action() {
            public String go(String username, String data) {
                Player p = players.get(username);
                if(p.getPhase().equals("discard")) {
                    if(playerOrder.indexOf(username) == dealer) {
                        Card card = Card.fromString(data);
                        if(card == null)
                            return "invalid data";
                        p.removeCard(card);
                        setPhaseAll("tricks");
                        return "true";
                    }
                }
                return "false";
            }
        });
    }};
}
