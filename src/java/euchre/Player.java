
package euchre;

import java.util.ArrayList;

public class Player {
    ArrayList<Card> cards;
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

    public String getTeammate() {
        return teammate;
    }

    public void setTeammate(String teammate) {
        this.teammate = teammate;
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
    
    public String getPassword() {
        return password;
    }
}
