package controller.menucontroller;

import controller.duel.singlePlayer.GameController;
import models.Game;
import models.Player;
import models.User;
import models.cards.Card;
import view.Regex;
import view.StatusEnum;

import java.util.regex.Matcher;

public class CheatMenuController {

    public String run(String command, String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        Player cheater;
        if (game.isMultiPlayer())
            cheater = game.getPlayerInTurn();
        else
            cheater = GameController.player;
        Matcher matcher;
        if ((matcher = Regex.getMatcher(command, Regex.CHEAT_INCREASE_MONEY)).matches())
            return increaseMoney(Integer.parseInt(matcher.group(2)), cheater);
        else if ((matcher = Regex.getMatcher(command, Regex.CHEAT_INCREASE_LP)).matches())
            return increaseLP(Integer.parseInt(matcher.group(2)), cheater);
        else if ((matcher = Regex.getMatcher(command, Regex.CHEAT_SELECT_MORE_CARDS_1)).matches())
            return selectCardForce(matcher.group(2), cheater, game);
        else if ((matcher = Regex.getMatcher(command, Regex.CHEAT_SELECT_MORE_CARDS_2)).matches())
            return selectCardForce(matcher.group(3), cheater, game);
        else
            return StatusEnum.INVALID_COMMAND.getStatus();
    }


    private String increaseMoney(int amount, Player cheater) {
        User cheaterUser = User.getUserByUserName(cheater.getUserName());
        assert cheaterUser != null;
        cheaterUser.setMoney(cheaterUser.getMoney() + amount);
        return "Cheat Activated Successfully";
    }

    private String increaseLP(int amount, Player cheater) {
        cheater.getPlayerBoard().setLifePoints(cheater.getPlayerBoard().getLifePoints() + amount);
        return "Cheat Activated Successfully";
    }

    private String selectCardForce(String cardName, Player cheater, Game game) {
        for (Card card: cheater.getPlayerBoard().getHandCards())
            if (card.getName().equals(cardName))
                game.setSelectedCard(card);
        return "Cheat Activated Successfully";
    }
}
