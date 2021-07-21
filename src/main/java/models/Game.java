package models;

import controller.duel.GamePhase;
import models.cards.Card;

import java.util.ArrayList;

public class Game {

    private int rounds;
    private int player1RoundWins = 0;
    private int player2RoundWins = 0;
    private static final ArrayList<Game> allGames = new ArrayList<>();
    private Board board1;
    private Board board2;
    private Player player1;
    private Player player2;
    private String token1;
    private String token2;
    private Player playerInTurn;
    private Player playerAgainst;
    private GamePhase currentPhase = GamePhase.DRAW;
    private Card selectedCard;
    private boolean isMultiPlayer;
    private boolean isFirstPlay;

    public Game(Board board1, Board board2, Player player1, Player player2, String token1, String token2, boolean isMultiPlayer) {
        allGames.add(this);
        this.setBoard1(board1);
        this.setBoard2(board2);
        this.setPlayer1(player1);
        this.setPlayer2(player2);
        this.setToken1(token1);
        this.setToken2(token2);
        this.setRounds(rounds);
        this.setMultiPlayer(isMultiPlayer);
    }

    public static void removeGame(Game game) {
        allGames.remove(game);
    }

    public static Game getGameByToken(String token) {
        for (Game game : allGames) {
            if (game.token1.equals(token) || game.token2.equals(token))
                return game;
        }
        return null;
    }

    public Board getBoard1() {
        return board1;
    }

    public void setBoard1(Board board1) {
        this.board1 = board1;
    }

    public Board getBoard2() {
        return board2;
    }

    public void setBoard2(Board board2) {
        this.board2 = board2;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public String getToken1() {
        return token1;
    }

    public void setToken1(String token1) {
        this.token1 = token1;
    }

    public String getToken2() {
        return token2;
    }

    public void setToken2(String token2) {
        this.token2 = token2;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getRounds() {
        return this.rounds;
    }

    public boolean isMultiPlayer() {
        return isMultiPlayer;
    }

    public void setMultiPlayer(boolean multiPlayer) {
        isMultiPlayer = multiPlayer;
    }

    public void setPlayerInTurn(Player playerInTurn) {
        this.playerInTurn = playerInTurn;
    }

    public Player getPlayerInTurn() {
        return this.playerInTurn;
    }

    public void setPlayerAgainst(Player playerAgainst) {
        this.playerAgainst = playerAgainst;
    }

    public Player getPlayerAgainst() {
        return this.playerAgainst;
    }

    public boolean isPlayerInTurn(String token) {
        if (playerInTurn.equals(player1) && token1.equals(token))
            return true;
        return playerInTurn.equals(player2) && token2.equals(token);
    }

    public void setCurrentPhase(GamePhase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public GamePhase getCurrentPhase() {
        return this.currentPhase;
    }

    public void setSelectedCard(Card selectedCard) {
        this.selectedCard = selectedCard;
    }

    public Card getSelectedCard() {
        return this.selectedCard;
    }

    public int getPlayer1RoundWins() {
        return player1RoundWins;
    }

    public void increasePlayer1RoundWins() {
        this.player1RoundWins++;
    }

    public int getPlayer2RoundWins() {
        return player2RoundWins;
    }

    public void increasePlayer2RoundWins() {
        this.player2RoundWins++;
    }

    public boolean isFirstPlay() {
        return isFirstPlay;
    }

    public void setFirstPlay(boolean firstPlay) {
        isFirstPlay = firstPlay;
    }
}
