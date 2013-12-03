
package euchre;

import java.util.ArrayList;

public class Player {
    ArrayList<Card> cards;
    private int ourScore, ourTricks;
    final private String password;
    
    public Player(String password) {
        cards = new ArrayList<Card>();
        ourScore = ourTricks = 0;
        this.password = password;
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
