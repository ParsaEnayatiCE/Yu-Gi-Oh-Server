package controller.duel;

import controller.duel.effect.monsterseffect.ContinuousEffects;
import controller.duel.effect.monsterseffect.SummonEffects;
import controller.duel.singlePlayer.GameController;
import models.Board;
import models.EffectsStatus;
import models.Game;
import models.cards.Location;
import models.cards.monsters.*;
import view.StatusEnum;

public class SummonController {

    public static boolean hasSummonedInThisTurn = false;
    public static MonsterCard lastSummonedMonster;

    public String summon(String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (checkNormalSummonSetConditions(false, game) != null)
            return checkNormalSummonSetConditions(false, game);
        return finalizeSummon(game);
    }

    public String tributeSummon(String tributes, String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (checkNormalSummonSetConditions(false, game) != null)
            return checkNormalSummonSetConditions(false, game);
        MonsterCard selectedMonster = (MonsterCard) game.getSelectedCard();
        if (game.isMultiPlayer()) {
            if ((selectedMonster.getLevel() < 7 && game.getPlayerInTurn().getPlayerBoard().getMonsters().size() < 1)
                    || selectedMonster.getLevel() > 6 && game.getPlayerInTurn().getPlayerBoard().getMonsters().size() < 2)
                return "there are not enough cards for tribute";
        } else {
            if ((selectedMonster.getLevel() < 7 && GameController.player.getPlayerBoard().getMonsters().size() < 1)
                    || selectedMonster.getLevel() > 6 && GameController.player.getPlayerBoard().getMonsters().size() < 2)
                return "there are not enough cards for tribute";
        }
        int firstMonster, secondMonster = 0;
        if (tributes.length() == 1)
            firstMonster = Integer.parseInt(tributes);
        else {
            firstMonster = Integer.parseInt(tributes.substring(0, 1));
            secondMonster = Integer.parseInt(tributes.substring(2, 3));
        }
        if (game.isMultiPlayer()) {
            if (selectedMonster.getLevel() > 6 && secondMonster == 0
                    || (selectedMonster.getLevel() > 6 && (game.getPlayerInTurn().getPlayerBoard().getMonsterBoard().get(firstMonster - 1) == null
                    || game.getPlayerInTurn().getPlayerBoard().getMonsterBoard().get(secondMonster - 1) == null))
                    || selectedMonster.getLevel() < 7 && game.getPlayerInTurn().getPlayerBoard().getMonsterBoard().get(firstMonster - 1) == null)
                return "there are not enough monsters on these addresses";
        } else {
            if (selectedMonster.getLevel() > 6 && secondMonster == 0
                    || (selectedMonster.getLevel() > 6 && (GameController.player.getPlayerBoard().getMonsterBoard().get(firstMonster - 1) == null
                    || GameController.player.getPlayerBoard().getMonsterBoard().get(secondMonster - 1) == null))
                    || selectedMonster.getLevel() < 7 && GameController.player.getPlayerBoard().getMonsterBoard().get(firstMonster - 1) == null)
                return "there are not enough monsters on these addresses";
        }
        selectedMonster.setSummonType(SummonType.TRIBUTE_SUMMON);
        return finalizeSummon(game);
    }

    private String finalizeSummon(Game game) {
        MonsterCard selectedMonster = (MonsterCard) game.getSelectedCard();
        selectedMonster.setLocation(Location.FIELD);
        selectedMonster.setIsHidden(false);
        selectedMonster.setMode(Mode.ATTACK);
        if (game.isMultiPlayer())
            game.getPlayerInTurn().getPlayerBoard().summonOrSetMonster(selectedMonster);
        else
            GameController.player.getPlayerBoard().summonOrSetMonster(selectedMonster);
        hasSummonedInThisTurn = true;
        lastSummonedMonster = selectedMonster;
        if (game.isMultiPlayer())
            ContinuousEffects.run(game.getPlayerInTurn().getPlayerBoard(), game.getPlayerAgainst().getPlayerBoard());
        else
            ContinuousEffects.run(GameController.player.getPlayerBoard(), GameController.bot.getBoard());
        return "summoned successfully";
    }

    public static String checkNormalSummonSetConditions(boolean isSpecial, Game game) {
        if (game.getSelectedCard() == null)
            return "no card is selected yet";
        if (game.isMultiPlayer()) {
            if (!game.getPlayerInTurn().getPlayerBoard().getHandCards().contains(game.getSelectedCard())
                    || !(game.getSelectedCard() instanceof MonsterCard))
                return "you can't summon this card";
        } else {
            if (!GameController.player.getPlayerBoard().getHandCards().contains(game.getSelectedCard())
                    || !(game.getSelectedCard() instanceof MonsterCard))
                return "you can't summon this card";
        }
        MonsterCard selectedMonster = (MonsterCard) game.getSelectedCard();
        if (!isSpecial && selectedMonster.getLevel() > 4)
            return "you can't summon this card";
        if (game.isMultiPlayer()) {
            if (game.getCurrentPhase() != GamePhase.MAIN1 && game.getCurrentPhase() != GamePhase.MAIN2)
                return "action not allowed in this phase";
            if (game.getPlayerInTurn().getPlayerBoard().getMonsters().size() == 5)
                return "monster card zone is full";
        } else {
            if (GameController.currentPhase != GamePhase.MAIN1 && GameController.currentPhase != GamePhase.MAIN2)
                return "action not allowed in this phase";
            if (GameController.player.getPlayerBoard().getMonsters().size() == 5)
                return "monster card zone is full";
        }
        if (!isSpecial && hasSummonedInThisTurn)
            return "you already summoned/set on this turn";
        return null;
    }

    public String flipSummon(String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (!game.isPlayerInTurn(token))
            return "it's not your turn";
        if (game.getSelectedCard() == null)
            return "no card is selected yet";
        if (game.isMultiPlayer()) {
            if (!game.getPlayerInTurn().getPlayerBoard().getMonsters().contains((MonsterCard) game.getSelectedCard()))
                return "you can't change this card position";
        } else {
            if (!GameController.player.getPlayerBoard().getMonsters().contains((MonsterCard) game.getSelectedCard()))
                return "you can't change this card position";
        }
        MonsterCard selectedMonster = (MonsterCard) game.getSelectedCard();
        if (game.isMultiPlayer()) {
            if (game.getCurrentPhase() != GamePhase.MAIN1 && game.getCurrentPhase() != GamePhase.MAIN2)
                return "action not allowed in this phase";
        } else {
            if (GameController.currentPhase != GamePhase.MAIN1 && GameController.currentPhase != GamePhase.MAIN2)
                return "action not allowed in this phase";
        }
        if (!selectedMonster.getIsHidden())
            return "you can't flip this card";
        selectedMonster.setMode(Mode.ATTACK);
        selectedMonster.setIsHidden(false);
        lastSummonedMonster = selectedMonster;
        selectedMonster.setSummonType(SummonType.FLIP_SUMMON);
        if (game.isMultiPlayer()) {
            ContinuousEffects.run(game.getPlayerInTurn().getPlayerBoard(), game.getPlayerAgainst().getPlayerBoard());
            SummonEffects.run((MonsterCard) game.getSelectedCard(), game.getPlayerAgainst().getPlayerBoard(), game.getPlayerInTurn().getPlayerBoard());
        } else {
            ContinuousEffects.run(GameController.player.getPlayerBoard(), GameController.bot.getBoard());
            SummonEffects.run((MonsterCard) game.getSelectedCard(), GameController.bot.getBoard(), GameController.player.getPlayerBoard());
        }
        return "flip summoned successfully";
    }

    public String specialSummon(String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (checkNormalSummonSetConditions(true, game) != null)
            return checkNormalSummonSetConditions(true, game);
        if (checkSpecialSummonStatus(game) != null)
            return checkSpecialSummonStatus(game);
        return finalizeSummon(game);
    }

    private String checkSpecialSummonStatus(Game game) {
        EffectsStatus effectsStatus;
        if (game.isMultiPlayer())
            effectsStatus = game.getPlayerInTurn().getPlayerBoard().getEffectsStatus();
        else
            effectsStatus = GameController.player.getPlayerBoard().getEffectsStatus();
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (effectsStatus.getSpecialSummonStatus() == SpecialSummonStatus.NONE)
            return StatusEnum.NO_WAY_TO_SPECIAL_SUMMON.getStatus();
        if (effectsStatus.getSpecialSummonStatus() == SpecialSummonStatus.FROM_GRAVEYARD
                && monsterCard.getLocation() != Location.GRAVEYARD)
            return StatusEnum.NO_WAY_TO_SPECIAL_SUMMON.getStatus();
        if (effectsStatus.getSpecialSummonStatus() == SpecialSummonStatus.LEVEL7H_FROM_GRAVE
                && (monsterCard.getLocation() != Location.GRAVEYARD || monsterCard.getLevel() < 7))
            return StatusEnum.NO_WAY_TO_SPECIAL_SUMMON.getStatus();
        if (effectsStatus.getSpecialSummonStatus() == SpecialSummonStatus.NORMAL_CYBERSE
                && (monsterCard.getMonsterType() != MonsterType.CYBERSE || monsterCard.getTrait() != Trait.NORMAL))
            return StatusEnum.NO_WAY_TO_SPECIAL_SUMMON.getStatus();
        if (effectsStatus.getSpecialSummonStatus() == SpecialSummonStatus.NORMAL_LEVEL4L_FROM_HAND
                && (monsterCard.getLocation() != Location.HAND || monsterCard.getLevel() > 4 || monsterCard.getTrait() != Trait.NORMAL))
            return StatusEnum.NO_WAY_TO_SPECIAL_SUMMON.getStatus();
        monsterCard.setSummonType(SummonType.SPECIAL_SUMMON);
        effectsStatus.setSpecialSummonStatus(SpecialSummonStatus.NONE);
        return null;
    }

    public String ritualSummon(String monsterIndexes, String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        if (checkNormalSummonSetConditions(true, game) != null)
            return checkNormalSummonSetConditions(true, game);
        MonsterCard selectedMonster = (MonsterCard) game.getSelectedCard();
        Board board;
        if (game.isMultiPlayer())
            board = game.getPlayerInTurn().getPlayerBoard();
        else
            board = GameController.player.getPlayerBoard();
        if (selectedMonster.getTrait() != Trait.RITUAL || !board.getEffectsStatus().getCanRitualSummon())
            return StatusEnum.NO_WAY_TO_RITUAL.getStatus();
        String[] handIndexes = monsterIndexes.split(" ");
        int levelSum = 0;
        for (String handIndex : handIndexes) {
            if (!handIndex.matches("\\d") || Integer.parseInt(handIndex) >= board.getHandCards().size() ||
                    board.getHandCards().get(Integer.parseInt(handIndex)) == null ||
                    !(board.getHandCards().get(Integer.parseInt(handIndex)) instanceof MonsterCard))
                return StatusEnum.NO_WAY_TO_RITUAL.getStatus();
            else
                levelSum += ((MonsterCard) board.getHandCards().get(Integer.parseInt(handIndex))).getLevel();
        }
        if (levelSum != selectedMonster.getLevel())
            return StatusEnum.NO_WAY_TO_RITUAL.getStatus();
        board.getEffectsStatus().setCanRitualSummon(false);
        selectedMonster.setSummonType(SummonType.SPECIAL_SUMMON);
        return finalizeSummon(game);
    }
}
