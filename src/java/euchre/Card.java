
package euchre;

import java.util.Arrays;

public class Card {
    public String suit, type;
    
    public Card(String suit, String type) {
        this.suit = suit;
        this.type = type;
    }

    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.suit != null ? this.suit.hashCode() : 0);
        hash = 23 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Card other = (Card) obj;
        if ((this.suit == null) ? (other.suit != null) : !this.suit.equals(other.suit)) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        return true;
    }
    
    public static Card fromString(String str) {
        String s=null,t=null;
        if(str.length() == 2) {
            s = str.substring(1,2);
            t = str.substring(0,1);
        } else if(str.length() == 3) {
            s = str.substring(2,3);
            t = str.substring(0,2);
        } else
            return null;
        if(!Arrays.asList(Game.types).contains(t) || !Arrays.asList(Game.suits).contains(s))
            return null;
        return new Card(s,t);
    }
}
