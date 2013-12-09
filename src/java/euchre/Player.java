
package euchre;

import java.util.ArrayList;

public class Player {
    private ArrayList<Card> cards;
    private int ourScore, ourTricks;
    private String teammate, phase;
    final private String password;
    
    public Player(String password) {
        cards = new ArrayList<Card>();
        ourScore = ourTricks = 0;
        phase = "pregame";
        teammate = "";
        this.password = password;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }
    
    public void setScore(int s) {
        ourScore = s;
    }

    public String getTeammate() {
        return teammate;
    }

    public void setTeammate(String teammate) {
        this.teammate = teammate;
    }
    
    public void addCard(Card card) {
        cards.add(card);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public int getOurScore() {
        return ourScore;
    }

    public int getOurTricks() {
        return ourTricks;
    }
    
    public void addTrick() {
        ourTricks++;
    }
    public void addScore(int score) {
        ourScore += score;
        //cap @ 10?
    }
    
    public String getPassword() {
        return password;
    }
    
    public void clear() {
        cards.clear();
        ourTricks = 0;
    }
    
    public boolean hasCard(Card card) {
        return cards.contains(card);
    }
    
    public Card removeCard(Card card) {
        int idx = cards.indexOf(card);
        Card r = cards.get(idx);
        cards.remove(idx);
        return r;
    }
    
    public int cardCount() {
        return cards.size();
    }
}
