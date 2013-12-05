
package euchre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Game {
    private Map<String, Player> players;
    private ArrayList<String> playerOrder;
    private ArrayList<Card> onTable;
    private int playerTurn, dealer;
    private String trump;
    private int bidWinner;
    private Card trumpCard;
    private Card[] deck;
    private int deckposn;
    
    public Game() {
        players = new HashMap<String, Player>();
        playerOrder = new ArrayList<String>();
        onTable = new ArrayList<Card>();
        clearVars();
        deck = new Card[52];
        String[] suits = new String[]{"c","d","h","s"};
        String[] types = new String[]{"a","k","q","j","10","9"};
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
        return bean;
    }

    public String tryAction(String username, String action, String actiondata) {
        if(username == null || username.equals(""))
            return "invalid";
        
        Action a = actionMap.get(action);
        if(a == null)
            return "invalid";
        else
            return a.go(username, actiondata) ? "true" : "false";
    }
    
    private Card draw() {
        return deck[deckposn++];
    }
    
    private interface Action {
        public boolean go(String username, String data);
    }
    
    private void setPhaseAll(String phase) {
        for(Player pp : players.values())
            pp.setPhase(phase);
    }
    
    private Map<String, Action> actionMap = new HashMap<String, Action>() {{
        put("deal", new Action() {
            public boolean go(String username, String data) {
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
                    return true;
                }
                return false;
            }
        });
        put("play", new Action() {
            public boolean go(String username, String data) {
                return false;
            }
        });
        put("declare trump", new Action() {
            public boolean go(String username, String data) {
                return false;
            }
        });
        put("pass", new Action() {
            public boolean go(String username, String data) {
                return false;
            }
        });
        put("begin", new Action() {
            public boolean go(String username, String data) {
                return false;
            }
        });
        put("request teammate", new Action() {
            public boolean go(String username, String data) {
                return false;
            }
        });
    }};
}
