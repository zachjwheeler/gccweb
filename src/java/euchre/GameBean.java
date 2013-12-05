package euchre;

import java.util.ArrayList;

public class GameBean implements java.io.Serializable {
    private ArrayList<String> players;
    private ArrayList<Card> cards, onTable;
    private int playerTurn;//0 means my turn
    private int dealer;//0 means me
    private String trump;
    private int bidWinner, ourScore, theirScore, ourTricks, theirTricks;
    private Card trumpCard;
    private String teammate, phase;
    private boolean alone;

    public boolean isAlone() {
        return alone;
    }

    public void setAlone(boolean alone) {
        this.alone = alone;
    }

    public String getTeammate() {
        return teammate;
    }

    public void setTeammate(String teammate) {
        this.teammate = teammate;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public Card getTrumpCard() {
        return trumpCard;
    }

    public void setTrumpCard(Card trumpCard) {
        this.trumpCard = trumpCard;
    }

    public int getBidWinner() {
        return bidWinner;
    }

    public void setBidWinner(int bidWinner) {
        this.bidWinner = bidWinner;
    }

    public int getOurScore() {
        return ourScore;
    }

    public void setOurScore(int ourScore) {
        this.ourScore = ourScore;
    }

    public int getTheirScore() {
        return theirScore;
    }

    public void setTheirScore(int theirScore) {
        this.theirScore = theirScore;
    }

    public int getOurTricks() {
        return ourTricks;
    }

    public void setOurTricks(int ourTricks) {
        this.ourTricks = ourTricks;
    }

    public int getTheirTricks() {
        return theirTricks;
    }

    public void setTheirTricks(int theirTricks) {
        this.theirTricks = theirTricks;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<String> players) {
        this.players = players;
    }

    public ArrayList<Card> getOnTable() {
        return onTable;
    }

    public void setOnTable(ArrayList<Card> onTable) {
        this.onTable = onTable;
    }

    public int getDealer() {
        return dealer;
    }

    public void setDealer(int dealer) {
        this.dealer = dealer;
    }

    public String getTrump() {
        return trump;
    }

    public void setTrump(String trump) {
        this.trump = trump;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }
}
