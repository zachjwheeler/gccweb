
package euchre;

import java.util.Arrays;

public class Card {
    public String suit, type;
    
    public Card(String suit, String type) {
        this.suit = suit;
        this.type = type;
    }
    
    public boolean equals(Card o) {
        return suit.equals(o.suit) && type.equals(o.type);
    }
    
    public static Card fromString(String str) {
        if(str.length() != 2)
            return null;
        String s = str.substring(0,1), t = str.substring(1,2);
        if(Arrays.asList(Game.types).contains(t) || Arrays.asList(Game.suits).contains(s))
            return null;
        return new Card(s,t);
    }
}
