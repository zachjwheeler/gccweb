
package euchre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Game {
    private Map<String, Player> players;
    private ArrayList<String> playerOrder;
    private int playerTurn;
    private int dealer;
    private String trump;
    private ArrayList<Card> onTable;
    private int bidWinner;
    private Card[] deck;
    
    public Game() {
        players = new HashMap<String, Player>();
        playerOrder = new ArrayList<String>();
        onTable = new ArrayList<Card>();
        playerTurn = dealer = 0;
        bidWinner = -1;
        trump = "";
        deck = new Card[52];
        String[] suits = new String[]{"c","d","h","s"};
        String[] types = new String[]{"a","k","q","j","10","9","8","7","6","5","4","3","2"};
        int idx=0;
        for(String suit : suits) {
            for(String type : types) {
                deck[idx++] = new Card(suit, type);
            }
        }
    }
    
    public void shuffle() {
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
    }
    private void endGame() {
        players.clear();
        playerOrder.clear();
        onTable.clear();
        playerTurn = dealer = 0;
        bidWinner = -1;
        trump = "";
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
        return (me - v + players.size()) % players.size();
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
        bean.setOnTable(onTable);
        bean.setBidWinner(bidWinner);
        bean.setOurScore(player.getOurScore());
        bean.setOurTricks(player.getOurTricks());
        Player opponent = players.get(playerOrder.get((me+1)%playerOrder.size()));
        bean.setTheirScore(opponent.getOurScore());
        bean.setTheirTricks(opponent.getOurTricks());
        return bean;
    }

    public String tryAction(String action) {
        
        return "invalid";
    }
}
