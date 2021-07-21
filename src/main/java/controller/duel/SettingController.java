package controller.duel;

import controller.duel.singlePlayer.GameController;
import models.Game;
import models.cards.Location;
import models.cards.monsters.Mode;
import models.cards.monsters.MonsterCard;
import models.cards.monsters.SummonType;
import models.cards.spelltrap.Icon;
import models.cards.spelltrap.SpellTrapCard;

public class SettingController {


    public String set(String token) {
        Game game = Game.getGameByToken(token);
        if (game.getSelectedCard() == null)
            return "no card is selected yet";
        if (game.getSelectedCard() instanceof MonsterCard)
            return setMonster(game);
        else
            return setSpellTrap(game);
    }

    private String setSpellTrap(Game game) {
        SpellTrapCard selectedSpellTrap = (SpellTrapCard) game.getSelectedCard();
        if (game.isMultiPlayer()) {
            if (!game.getPlayerInTurn().getPlayerBoard().getHandCards().contains(game.getSelectedCard()))
                return "you can't set this card";
            if (game.getCurrentPhase() != GamePhase.MAIN1 && game.getCurrentPhase() != GamePhase.MAIN2)
                return "you can't do this action in this phase";
            if (game.getPlayerInTurn().getPlayerBoard().getSpellTraps().size() == 5 && selectedSpellTrap.getIcon() != Icon.FIELD)
                return "spell card zone is full";
        } else {
            if (!GameController.player.getPlayerBoard().getHandCards().contains(game.getSelectedCard()))
                return "you can't set this card";
            if (GameController.currentPhase != GamePhase.MAIN1 && GameController.currentPhase != GamePhase.MAIN2)
                return "you can't do this action in this phase";
            if (GameController.player.getPlayerBoard().getSpellTraps().size() == 5 && selectedSpellTrap.getIcon() != Icon.FIELD)
                return "spell card zone is full";
        }
        selectedSpellTrap.setLocation(Location.FIELD);
        selectedSpellTrap.setIsHidden(true);
        if (game.isMultiPlayer()) {
            if (selectedSpellTrap.getIcon() == Icon.FIELD)
                game.getPlayerInTurn().getPlayerBoard().setFieldZone(selectedSpellTrap);
            else
                game.getPlayerInTurn().getPlayerBoard().summonOrSetSpellAndTrap(selectedSpellTrap);
        } else {
            if (selectedSpellTrap.getIcon() == Icon.FIELD)
                GameController.player.getPlayerBoard().setFieldZone(selectedSpellTrap);
            else
                GameController.player.getPlayerBoard().summonOrSetSpellAndTrap(selectedSpellTrap);
        }
        return "set successfully";
    }

    private String setMonster(Game game) {
        if (SummonController.checkNormalSummonSetConditions(false, game) != null)
            return SummonController.checkNormalSummonSetConditions(false, game);
        MonsterCard selectedMonster = (MonsterCard) game.getSelectedCard();
        selectedMonster.setLocation(Location.FIELD);
        selectedMonster.setIsHidden(true);
        selectedMonster.setMode(Mode.DEFENSE);
        if (game.isMultiPlayer())
            game.getPlayerInTurn().getPlayerBoard().summonOrSetMonster(selectedMonster);
        else
            GameController.player.getPlayerBoard().summonOrSetMonster(selectedMonster);
        SummonController.hasSummonedInThisTurn = true;
        selectedMonster.setSummonType(SummonType.NORMAL_SET);
        return "set successfully";
    }

    public String changePosition(String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (game.getSelectedCard() == null)
            return "no card is selected yet";
        if (game.isMultiPlayer()) {
            if (!game.getPlayerInTurn().getPlayerBoard().getMonsters().contains((MonsterCard) game.getSelectedCard()))
                return "you can't change this card position";
            if (game.getCurrentPhase() != GamePhase.MAIN1 && game.getCurrentPhase() != GamePhase.MAIN2)
                return "you can't do this action in this phase";
        } else {
            if (!GameController.player.getPlayerBoard().getMonsters().contains((MonsterCard) game.getSelectedCard()))
                return "you can't change this card position";
            if (GameController.currentPhase != GamePhase.MAIN1 && GameController.currentPhase != GamePhase.MAIN2)
                return "you can't do this action in this phase";
        }
        MonsterCard selectedMonster = (MonsterCard) game.getSelectedCard();
        if (selectedMonster.getMode() == Mode.ATTACK)
            selectedMonster.setMode(Mode.DEFENSE);
        else
            selectedMonster.setMode(Mode.ATTACK);
        return "monster card position changed successfully";
    }
}
