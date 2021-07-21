package controller.duel;

import controller.duel.effect.CustomEffects;
import controller.duel.singlePlayer.GameController;
import controller.duel.effect.spells.*;
import controller.duel.effect.traps.MagicJammer;
import controller.duel.effect.traps.NormalTraps;
import controller.duel.effect.traps.SummonTraps;
import controller.duel.effect.traps.TimeSeal;
import models.Board;
import models.Game;
import models.cards.CardType;
import models.cards.monsters.MonsterCard;
import models.cards.spelltrap.SpellTrapCard;
import view.StatusEnum;

public class ActivationController {

    public String equip(int monsterIndex, String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (checkActivationConditions(game) != null)
            return checkActivationConditions(game);
        if (game.isMultiPlayer()) {
            if (game.getCurrentPhase() != GamePhase.MAIN2 && game.getCurrentPhase() != GamePhase.MAIN1)
                return StatusEnum.CANT_DO_THIS_ACTION_IN_THIS_PHASE.getStatus();
        } else {
            if (GameController.currentPhase != GamePhase.MAIN2 && GameController.currentPhase != GamePhase.MAIN1)
                return StatusEnum.CANT_DO_THIS_ACTION_IN_THIS_PHASE.getStatus();
        }
        if (monsterIndex > 5)
            return StatusEnum.INVALID_SELECTION.getStatus();
        MonsterCard monsterCard;
        if (game.isMultiPlayer())
            monsterCard = game.getPlayerInTurn().getPlayerBoard().getMonsterBoard().get(monsterIndex - 1);
        else
            monsterCard = GameController.player.getPlayerBoard().getMonsterBoard().get(monsterIndex - 1);
        if (monsterCard == null)
            return StatusEnum.NO_CARD_FOUND_IN_POSITION.getStatus();
        boolean hasAffected = EquipSpells.equip((SpellTrapCard) game.getSelectedCard(), monsterCard);
        if (!hasAffected)
            return "wrong selection";
        return StatusEnum.SPELL_ACTIVATED.getStatus();
    }

    public String activate(String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (checkActivationConditions(game) != null)
            return checkActivationConditions(game);
        if (game.isMultiPlayer()) {
            if (CustomEffects.activate(game.getSelectedCard(), game.getPlayerInTurn().getPlayerBoard(),
                    game.getPlayerAgainst().getPlayerBoard()))
                return "spell activated";
        } else {
            if (CustomEffects.activate(game.getSelectedCard(), GameController.player.getPlayerBoard(),
                    GameController.bot.getBoard()))
                return "spell activated";
        }
        if (game.getSelectedCard().getCardType() == CardType.SPELL)
            return activateSpell(game);
        else
            return activateTrap(game);
    }

    public String activateTrap(Game game) {
        if (checkActivationConditions(game) != null)
            return checkActivationConditions(game);
        SpellTrapCard trapCard = (SpellTrapCard) game.getSelectedCard();
        Board myBoard;
        Board rivalBoard;
        if (game.isMultiPlayer()) {
            myBoard = game.getPlayerInTurn().getPlayerBoard();
            rivalBoard = game.getPlayerAgainst().getPlayerBoard();
            if ((game.getCurrentPhase() == GamePhase.RIVAL_TURN && !trapCard.getIsHidden())
                    || (game.getCurrentPhase() != GamePhase.MAIN1 && game.getCurrentPhase() != GamePhase.MAIN2))
                return StatusEnum.CANT_DO_THIS_ACTION_IN_THIS_PHASE.getStatus();
        } else {
            myBoard = GameController.player.getPlayerBoard();
            rivalBoard = GameController.bot.getBoard();
            if ((GameController.currentPhase == GamePhase.RIVAL_TURN && !trapCard.getIsHidden())
                    || (GameController.currentPhase != GamePhase.MAIN1 && GameController.currentPhase != GamePhase.MAIN2))
                return StatusEnum.CANT_DO_THIS_ACTION_IN_THIS_PHASE.getStatus();
        }
        if (MagicJammer.activate(trapCard, myBoard, rivalBoard))
            return StatusEnum.SPELL_OR_TRAP_ACTIVATED.getStatus();
        else if (NormalTraps.activate(trapCard, myBoard, rivalBoard))
            return StatusEnum.SPELL_OR_TRAP_ACTIVATED.getStatus();
        else if (SummonTraps.activate(trapCard, SummonController.lastSummonedMonster, myBoard, rivalBoard))
            return StatusEnum.SPELL_OR_TRAP_ACTIVATED.getStatus();
        else if (TimeSeal.activate(trapCard, myBoard))
            return StatusEnum.SPELL_OR_TRAP_ACTIVATED.getStatus();
        return "trap can't be activated";
    }

    public String activateSpell(Game game) {
        if (checkActivationConditions(game) != null)
            return checkActivationConditions(game);
        SpellTrapCard spellCard = (SpellTrapCard) game.getSelectedCard();

        if (game.isMultiPlayer()) {
            if (game.getCurrentPhase() == GamePhase.RIVAL_TURN && game.getSelectedCard().getIsHidden()) {
                if (QuickPlays.activate(spellCard, game.getPlayerInTurn().getPlayerBoard(), game.getPlayerAgainst().getPlayerBoard()))
                    return StatusEnum.SPELL_ACTIVATED.getStatus();
                else return StatusEnum.CANT_DO_THIS_ACTION_IN_THIS_PHASE.getStatus();
            }
            if (game.getCurrentPhase() != GamePhase.MAIN2 && game.getCurrentPhase() != GamePhase.MAIN1)
                return StatusEnum.CANT_DO_THIS_ACTION_IN_THIS_PHASE.getStatus();
            if (MessengerOfPeace.activate(spellCard, game.getPlayerInTurn().getPlayerBoard()))
                return StatusEnum.SPELL_ACTIVATED.getStatus();
            else if (NormalActivate.activate(spellCard, game.getPlayerInTurn().getPlayerBoard(), game.getPlayerAgainst().getPlayerBoard()))
                return StatusEnum.SPELL_ACTIVATED.getStatus();
            else if (TurnSpells.activate(spellCard, game.getPlayerInTurn().getPlayerBoard(), AttackController.isAnyMonsterDead))
                return StatusEnum.SPELL_ACTIVATED.getStatus();
            else if (QuickPlays.activate(spellCard, game.getPlayerInTurn().getPlayerBoard(), game.getPlayerAgainst().getPlayerBoard()))
                return StatusEnum.SPELL_ACTIVATED.getStatus();
            else if (RingOfDefense.activate(spellCard, game.getPlayerInTurn().getPlayerBoard()))
                return StatusEnum.SPELL_ACTIVATED.getStatus();
        } else {
            if (GameController.currentPhase == GamePhase.RIVAL_TURN && game.getSelectedCard().getIsHidden()) {
                if (QuickPlays.activate(spellCard, GameController.player.getPlayerBoard(), GameController.bot.getBoard()))
                    return StatusEnum.SPELL_ACTIVATED.getStatus();
                else return StatusEnum.CANT_DO_THIS_ACTION_IN_THIS_PHASE.getStatus();
            }
            if (GameController.currentPhase != GamePhase.MAIN2 && GameController.currentPhase != GamePhase.MAIN1)
                return StatusEnum.CANT_DO_THIS_ACTION_IN_THIS_PHASE.getStatus();
            if (MessengerOfPeace.activate(spellCard, GameController.player.getPlayerBoard()))
                return StatusEnum.SPELL_ACTIVATED.getStatus();
            else if (NormalActivate.activate(spellCard, GameController.player.getPlayerBoard(), GameController.bot.getBoard()))
                return StatusEnum.SPELL_ACTIVATED.getStatus();
            else if (TurnSpells.activate(spellCard, GameController.player.getPlayerBoard(), AttackController.isAnyMonsterDead))
                return StatusEnum.SPELL_ACTIVATED.getStatus();
            else if (QuickPlays.activate(spellCard, GameController.player.getPlayerBoard(), GameController.bot.getBoard()))
                return StatusEnum.SPELL_ACTIVATED.getStatus();
            else if (RingOfDefense.activate(spellCard, GameController.player.getPlayerBoard()))
                return StatusEnum.SPELL_ACTIVATED.getStatus();
        }

        return StatusEnum.PREPARATION_OF_SPELL_NOT_DONE.getStatus();
    }

    public String activateOnMonster(int monsterIndex, String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (checkActivationConditions(game) != null)
            return checkActivationConditions(game);
        if (game.isMultiPlayer()) {
            if (game.getCurrentPhase() != GamePhase.MAIN2 && game.getCurrentPhase() != GamePhase.MAIN1)
                return StatusEnum.CANT_DO_THIS_ACTION_IN_THIS_PHASE.getStatus();
            if (OnMonsterSpells.activate((SpellTrapCard) game.getSelectedCard(),
                    game.getPlayerInTurn().getPlayerBoard(), game.getPlayerAgainst().getPlayerBoard(), monsterIndex))
                return StatusEnum.SPELL_ACTIVATED.getStatus();
        } else {
            if (GameController.currentPhase != GamePhase.MAIN2 && GameController.currentPhase != GamePhase.MAIN1)
                return StatusEnum.CANT_DO_THIS_ACTION_IN_THIS_PHASE.getStatus();
            if (OnMonsterSpells.activate((SpellTrapCard) game.getSelectedCard(),
                    GameController.player.getPlayerBoard(), GameController.bot.getBoard(), monsterIndex))
                return StatusEnum.SPELL_ACTIVATED.getStatus();
        }
        return "wrong selection";
    }

    private String checkActivationConditions(Game game) {
        if (game.getSelectedCard() == null)
            return StatusEnum.NO_CARD_SELECTED_YET.getStatus();
        if (game.getSelectedCard().getCardType() == CardType.MONSTER)
            return "activate is not for monsters";
        return null;
    }
}
