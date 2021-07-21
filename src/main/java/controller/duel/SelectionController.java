package controller.duel;

import controller.duel.singlePlayer.GameController;
import models.Game;
import view.StatusEnum;

public class SelectionController {

    public String selectMyMonster(String monsterNum, String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        int monsterIndex = Integer.parseInt(monsterNum);
        if (monsterIndex > 5)
            return StatusEnum.INVALID_SELECTION.getStatus();
        if (game.isMultiPlayer())
            game.setSelectedCard(game.getPlayerInTurn().getPlayerBoard().getMonsterBoard().get(monsterIndex - 1));
        else
            game.setSelectedCard(GameController.player.getPlayerBoard().getMonsterBoard().get(monsterIndex - 1));
        if (game.getSelectedCard() == null)
            return StatusEnum.NO_CARD_FOUND_IN_POSITION.getStatus();
        return StatusEnum.CARD_SELECTED.getStatus();
    }

    public String selectRivalMonster(String monsterNum, String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        int monsterIndex = Integer.parseInt(monsterNum);
        if (monsterIndex > 5)
            return StatusEnum.INVALID_SELECTION.getStatus();
        if (game.isMultiPlayer())
            game.setSelectedCard(game.getPlayerAgainst().getPlayerBoard().getMonsterBoard().get(monsterIndex - 1));
        else
            game.setSelectedCard(GameController.bot.getBoard().getMonsterBoard().get(monsterIndex - 1));
        if (game.getSelectedCard() == null)
            return StatusEnum.NO_CARD_FOUND_IN_POSITION.getStatus();
        return StatusEnum.CARD_SELECTED.getStatus();
    }

    public String selectMySpell(String spellNum, String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        int spellIndex = Integer.parseInt(spellNum);
        if (spellIndex > 5)
            return StatusEnum.INVALID_SELECTION.getStatus();
        if (game.isMultiPlayer())
            game.setSelectedCard(game.getPlayerInTurn().getPlayerBoard().getSpellAndTrapBoard().get(spellIndex - 1));
        else
            game.setSelectedCard(GameController.player.getPlayerBoard().getSpellAndTrapBoard().get(spellIndex - 1));
        if (game.getSelectedCard() == null)
            return StatusEnum.NO_CARD_FOUND_IN_POSITION.getStatus();
        return StatusEnum.CARD_SELECTED.getStatus();
    }

    public String selectRivalSpell(String spellNum, String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        int spellIndex = Integer.parseInt(spellNum);
        if (spellIndex > 5)
            return StatusEnum.INVALID_SELECTION.getStatus();
        if (game.isMultiPlayer())
            game.setSelectedCard(game.getPlayerAgainst().getPlayerBoard().getSpellAndTrapBoard().get(spellIndex - 1));
        else
            game.setSelectedCard(GameController.bot.getBoard().getSpellAndTrapBoard().get(spellIndex - 1));
        if (game.getSelectedCard() == null)
            return StatusEnum.NO_CARD_FOUND_IN_POSITION.getStatus();
        return StatusEnum.CARD_SELECTED.getStatus();
    }

    public String selectMyFieldCard(String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (game.isMultiPlayer())
            game.setSelectedCard(game.getPlayerInTurn().getPlayerBoard().getFieldZone());
        else
            game.setSelectedCard(GameController.player.getPlayerBoard().getFieldZone());
        if (game.getSelectedCard() == null)
            return StatusEnum.NO_CARD_FOUND_IN_POSITION.getStatus();
        return StatusEnum.CARD_SELECTED.getStatus();
    }

    public String selectRivalFieldCard(String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (game.isMultiPlayer())
            game.setSelectedCard(game.getPlayerAgainst().getPlayerBoard().getFieldZone());
        else
            game.setSelectedCard(GameController.bot.getBoard().getFieldZone());
        if (game.getSelectedCard() == null)
            return StatusEnum.NO_CARD_FOUND_IN_POSITION.getStatus();
        return StatusEnum.CARD_SELECTED.getStatus();
    }

    public String selectHandCard(String cardNum, String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        int index = Integer.parseInt(cardNum);
        if (game.isMultiPlayer()) {
            if (index > game.getPlayerInTurn().getPlayerBoard().getHandCards().size())
                return StatusEnum.INVALID_SELECTION.getStatus();
            game.setSelectedCard(game.getPlayerInTurn().getPlayerBoard().getHandCards().get(index - 1));
        } else {
            if (index > GameController.player.getPlayerBoard().getHandCards().size())
                return StatusEnum.INVALID_SELECTION.getStatus();
            game.setSelectedCard(GameController.player.getPlayerBoard().getHandCards().get(index - 1));
        }
        return StatusEnum.CARD_SELECTED.getStatus();
    }

    public String deSelect(String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (game.getSelectedCard() == null)
            return StatusEnum.NO_CARD_IS_SELECTED_YET.getStatus();
        game.setSelectedCard(null) ;
        return StatusEnum.CARD_DESELECTED.getStatus();
    }
}
