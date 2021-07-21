package controller.duel;

import controller.duel.effect.monsterseffect.ContinuousEffects;
import controller.duel.effect.monsterseffect.TurnEffects;
import controller.duel.singlePlayer.AI;
import controller.duel.effect.spells.OnMonsterSpells;
import controller.duel.effect.spells.FiledSpells;
import controller.duel.effect.spells.MessengerOfPeace;
import controller.duel.effect.spells.TurnSpells;
import models.Chain;
import models.Game;
import models.Player;
import models.User;
import models.cards.Card;
import view.StatusEnum;

public class PhaseController {

    public void startTheGame(String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        int coin = (int) (Math.random() * 2);
        if (coin == 0) {
            game.setPlayerInTurn(Player.getFirstPlayer());
            game.setPlayerAgainst(Player.getSecondPlayer());
        } else {
            game.setPlayerInTurn(Player.getSecondPlayer());
            game.setPlayerAgainst(Player.getFirstPlayer());
        }
        System.out.println("Coin has Flipped and " + game.getPlayerInTurn().getNickName() + " goes First");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(printBoard(game));
    }

    public String printBoard(Game game) {
        String middleLine = "\n--------------------------\n";
        if (!game.isMultiPlayer()) {
            String playerBoard = Player.getFirstPlayer().getPlayerBoard().toString();
            String botBoard = AI.getInstance().getBoard().reverseToString();
            return botBoard + middleLine + playerBoard;
        }
            String firstBoard = game.getPlayerInTurn().getPlayerBoard().toString();
            String secondBoard = game.getPlayerAgainst().getPlayerBoard().reverseToString();
            return secondBoard + middleLine + firstBoard;
    }

    public String changePhase(String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        findNextPhase(game);
        AttackController.alreadyAttackedCards.clear();
        AttackController.isBattleHappened = false;
        StringBuilder result = new StringBuilder(game.getCurrentPhase().getLabel());
        if (game.getCurrentPhase() == GamePhase.DRAW) {
            SummonController.hasSummonedInThisTurn = false;
            Player keepPlayer = game.getPlayerInTurn();
            game.setPlayerInTurn(game.getPlayerAgainst());
            game.setPlayerAgainst(keepPlayer);
            if (game.getPlayerAgainst().getPlayerBoard().getEffectsStatus().getCanRivalPickCard()) {
                Card card = game.getPlayerInTurn().getPlayerBoard().drawCard();
                if (card == null)
                    endGame(game.getPlayerAgainst(), game.getPlayerInTurn(), game);
                assert card != null;
                result.append("\nnew card added to hand: ").append(card.getName());
            }
            else result.append("\n can't pick card");
        }
        else if (game.getCurrentPhase() == GamePhase.STANDBY) {
            TurnEffects.run(game.getPlayerInTurn().getPlayerBoard(), game.getPlayerAgainst().getPlayerBoard());
            FiledSpells.check(game.getPlayerInTurn().getPlayerBoard(), game.getPlayerAgainst().getPlayerBoard());
            MessengerOfPeace.checkStandBy(game.getPlayerInTurn().getPlayerBoard());
            game.getPlayerAgainst().getPlayerBoard().getEffectsStatus().setCanRivalPickCard(true);
        }
        else if (game.getCurrentPhase() == GamePhase.BATTLE)
            ContinuousEffects.run(game.getPlayerInTurn().getPlayerBoard(), game.getPlayerAgainst().getPlayerBoard());
        else if (game.getCurrentPhase() == GamePhase.RIVAL_TURN) {
            result.append("\nit's ").append(game.getPlayerAgainst().getNickName()).append("'s turn to activate quick spells or traps");
            Player keepPlayer = game.getPlayerInTurn();
            game.setPlayerInTurn(game.getPlayerAgainst());
            game.setPlayerAgainst(keepPlayer);
        }
        else if (game.getCurrentPhase() == GamePhase.MAIN2) {
            result.append("\nit's ").append(game.getPlayerAgainst().getNickName()).append("'s turn again to respond to rival");
            Player keepPlayer = game.getPlayerInTurn();
            game.setPlayerInTurn(game.getPlayerAgainst());
            game.setPlayerAgainst(keepPlayer);
        }
        else if (game.getCurrentPhase() == GamePhase.END) {
            result.append("\nit's ").append(game.getPlayerAgainst().getNickName()).append("'s turn");
            ContinuousEffects.run(game.getPlayerInTurn().getPlayerBoard(), game.getPlayerAgainst().getPlayerBoard());
            Chain.activate();
            game.setSelectedCard(null);
            resetSomeEffects(game);
            SummonController.hasSummonedInThisTurn = false;
            game.setFirstPlay(false);
        }
        else if (game.getCurrentPhase() == GamePhase.SWITCH_CARDS1) {
            cardSwitched = 0;
            result.append("\nit's first player turn to switch cards between main and side deck");
            game.setPlayerInTurn(Player.getFirstPlayer());
            game.setPlayerAgainst(Player.getSecondPlayer());
        }
        else if (game.getCurrentPhase() == GamePhase.SWITCH_CARDS2) {
            cardSwitched = 0;
            result.append("\nit's second player turn to switch cards between main and side deck");
            game.setPlayerInTurn(Player.getSecondPlayer());
            game.setPlayerAgainst(Player.getFirstPlayer());
        }
        return result.toString();
    }

    private void resetSomeEffects(Game game) {
        game.getPlayerInTurn().getPlayerBoard().getEffectsStatus().setRivalTrapsBlocked(false);
        game.getPlayerAgainst().getPlayerBoard().getEffectsStatus().setRivalTrapsBlocked(false);
        AttackController.isAnyMonsterDead = false;
        AttackController.isBattleHappened = false;
        TurnSpells.checkTurn(game.getPlayerInTurn().getPlayerBoard());
        OnMonsterSpells.deactivate(game.getPlayerInTurn().getPlayerBoard(), game.getPlayerAgainst().getPlayerBoard());
    }

    private void findNextPhase(Game game) {
        if (game.getCurrentPhase() == GamePhase.DRAW)
            game.setCurrentPhase(GamePhase.STANDBY);
        else if (game.getCurrentPhase() == GamePhase.STANDBY)
            game.setCurrentPhase(GamePhase.MAIN1);
        else if (game.getCurrentPhase() == GamePhase.MAIN1)
            game.setCurrentPhase(GamePhase.BATTLE);
        else if (game.getCurrentPhase() == GamePhase.BATTLE)
            game.setCurrentPhase(GamePhase.RIVAL_TURN);
        else if (game.getCurrentPhase() == GamePhase.RIVAL_TURN)
            game.setCurrentPhase(GamePhase.MAIN2);
        else if (game.getCurrentPhase() == GamePhase.MAIN2)
            game.setCurrentPhase(GamePhase.END);
        else if (game.getCurrentPhase() == GamePhase.SWITCH_CARDS1)
            game.setCurrentPhase(GamePhase.SWITCH_CARDS2);
        else
            game.setCurrentPhase(GamePhase.DRAW);
    }

    public void endGame(Player winner, Player looser, Game game) {
        User winnerUser = winner.getUser();
        User loserUser = looser.getUser();
        if (game.getRounds() == 1) {
            winnerUser.setMoney(winnerUser.getMoney() + 1000 + winner.getPlayerBoard().getLifePoints());
            loserUser.setMoney(loserUser.getMoney() + 100);
            winnerUser.setScore(winnerUser.getScore() + 1000);
            Card.resetSwitch();
            Player.removePlayers();
            System.out.println(winnerUser.getUserName() + " won the whole match with score: 1-0");
        }
        else {
            if (winner.equals(Player.getFirstPlayer()))
                game.increasePlayer1RoundWins();
            else
                game.increasePlayer2RoundWins();
            winner.setMaxLifePoint(winner.getPlayerBoard().getLifePoints());
            looser.setMaxLifePoint(looser.getPlayerBoard().getLifePoints());
            if (game.getPlayer2RoundWins() == 2 || game.getPlayer1RoundWins() == 2) {
                loserUser.setMoney(loserUser.getMoney() + 300);
                winnerUser.setScore(winnerUser.getScore() + 3000);
                winnerUser.setMoney(winnerUser.getMoney() + winner.getMaxLifePoint() * 3 + 3000);
                Card.resetSwitch();
                Player.removePlayers();
                System.out.println(winnerUser.getUserName() + " won the whole match with score: " +
                        game.getPlayer1RoundWins() + "-" + game.getPlayer2RoundWins());
            }
            else {
                System.out.println(winnerUser.getUserName() + "won the game and the score is: " +
                        game.getPlayer1RoundWins() + "-" + game.getPlayer2RoundWins());
                game.setFirstPlay(true);
                game.setCurrentPhase(GamePhase.SWITCH_CARDS1);
            }
        }
    }

    private int cardSwitched = 0;
    public String switchCards(int mainCardIndex, int sideCardIndex, String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (game.getCurrentPhase() != GamePhase.SWITCH_CARDS1)
            return StatusEnum.CANT_DO_THIS_ACTION_IN_THIS_PHASE.getStatus();
        if (mainCardIndex >= game.getPlayerInTurn().getPlayerDeck().getMainDeck().size() ||
                sideCardIndex >= game.getPlayerInTurn().getPlayerDeck().getSideDeck().size())
            return "wrong index";
        if (game.getPlayerInTurn().getPlayerDeck().getMainDeck().get(mainCardIndex) == null ||
                game.getPlayerInTurn().getPlayerDeck().getSideDeck().get(sideCardIndex) == null)
            return "there is no cards in chosen index";
        if (cardSwitched == 2)
            return "you can't switch any more cards";
        if (game.getPlayerInTurn().getPlayerDeck().getMainDeck().get(mainCardIndex).getIsSwitched()
                || game.getPlayerInTurn().getPlayerDeck().getSideDeck().get(sideCardIndex).getIsSwitched())
            return "this card has already been switched";
        cardSwitched++;
        game.getPlayerInTurn().getPlayerDeck().getMainDeck().get(mainCardIndex).setSwitched(true);
        game.getPlayerInTurn().getPlayerDeck().getSideDeck().get(sideCardIndex).setSwitched(true);
        game.getPlayerInTurn().getPlayerBoard().resetTheBoard(mainCardIndex, sideCardIndex);
        return "switched cards successfully";
    }
}
