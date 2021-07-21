import controller.duel.*;
import controller.menucontroller.CheatMenuController;
import controller.menucontroller.LoginMenuController;
import controller.menucontroller.ProfileMenuController;
import models.Game;
import models.User;

public class InputOutput {


    private static final LoginMenuController loginMenuController = new LoginMenuController();
    private static final SummonController summonController = new SummonController();
    private static final SettingController settingController = new SettingController();
    private static final SelectionController selectionController = new SelectionController();
    private static final PhaseController phaseController = new PhaseController();
    private static final AttackController attackController = new AttackController();
    private static final ActivationController activationController = new ActivationController();

    public static String process(String input) {
        String[] inputParts = input.split("-");
        if (inputParts[0].equals("register"))
            return loginMenuController.createUser(inputParts[1], inputParts[2], inputParts[3]);
        else if (inputParts[0].equals("login")) {
            return loginMenuController.loginUSer(inputParts[1], inputParts[2]);
        } else if (inputParts[0].equals("changeUsername")) {
            String token = inputParts[inputParts.length - 1];
            String newUserName = inputParts[1];
            if (getLoggedInUserByToken(token) != null) {
                return new ProfileMenuController(User.getUserByUserName(token)).changeUsername(newUserName);
            } else {
                return "User is not logged in";
            }
        } else if (inputParts[0].equals("changePassword")) {
            String token = inputParts[inputParts.length - 1];
            String oldPass = inputParts[1];
            String newPass = inputParts[2];
            if (getLoggedInUserByToken(token) != null) {
                return new ProfileMenuController(getLoggedInUserByToken(token)).changePass(oldPass, newPass);
            } else {
                return "User is not logged in";
            }

        } else if (inputParts[0].equals("changeNickname")) {
            String token = inputParts[inputParts.length - 1];
            String newNickname = inputParts[1];
            if (getLoggedInUserByToken(token) != null) {
                return new ProfileMenuController(getLoggedInUserByToken(token)).changeNickname(newNickname);
            } else {
                return "User is not logged in";
            }
        } else if (inputParts[0].equals("logout")) {
            String token = inputParts[inputParts.length - 1];
            String username = inputParts[1];
            return loginMenuController.logout(token, username);
        }
        //GAME
        //SUMMON
        else if (inputParts[0].equals("flipSummon"))
            return summonController.flipSummon(inputParts[1]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("specialSummon"))
            return summonController.specialSummon(inputParts[1]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("summon"))
            return summonController.summon(inputParts[1]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("tributeSummon"))
            return summonController.tributeSummon(inputParts[1], inputParts[2]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("ritualSummon"))
            return summonController.ritualSummon(inputParts[1], inputParts[2]) + returnBoards(inputParts[inputParts.length - 1]);
        //SET
        else if (inputParts[0].equals("set"))
            return settingController.set(inputParts[1]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("changePosition"))
            return settingController.set(inputParts[1]) + returnBoards(inputParts[inputParts.length - 1]);
        //SELECTION
        else if (inputParts[0].equals("selectMyMonster"))
            return selectionController.selectMyMonster(inputParts[1], inputParts[2]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("selectRivalMonster"))
            return selectionController.selectRivalMonster(inputParts[1], inputParts[2]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("selectMySpell"))
            return selectionController.selectMySpell(inputParts[1], inputParts[2]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("selectRivalSpell"))
            return selectionController.selectRivalSpell(inputParts[1], inputParts[2]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("selectMyFiledCard"))
            return selectionController.selectMyFieldCard(inputParts[1]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("selectRivalFieldCard"))
            return selectionController.selectRivalFieldCard(inputParts[1]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("selectMyHandCard"))
            return selectionController.selectHandCard(inputParts[1], inputParts[2]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("deSelect"))
            return selectionController.deSelect(inputParts[1]) + returnBoards(inputParts[inputParts.length - 1]);
        //PHASE
        else if (inputParts[0].equals("changePhase"))
            return phaseController.changePhase(inputParts[1]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("switchCards"))
            return phaseController.switchCards(Integer.parseInt(inputParts[1]), Integer.parseInt(inputParts[2]),
                    inputParts[3]) + returnBoards(inputParts[inputParts.length - 1]);
        //ATTACK
        else if (inputParts[0].equals("attack"))
            return attackController.attackMonsterToMonster(inputParts[1], inputParts[2]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("directAttack"))
            return attackController.directAttack(inputParts[1]) + returnBoards(inputParts[inputParts.length - 1]);
        //ACTIVATE
        else if (inputParts[0].equals("equip"))
            return activationController.equip(Integer.parseInt(inputParts[1]), inputParts[2]) + returnBoards(inputParts[inputParts.length - 1]);
        else if (inputParts[0].equals("activate"))
            return activationController.activate(inputParts[1]);
        else if (inputParts[0].equals("activateOnMonster"))
            return activationController.activateOnMonster(Integer.parseInt(inputParts[1]), inputParts[2]) +
                    returnBoards(inputParts[inputParts.length - 1]);
        //CHEAT
        else if (inputParts[0].equals("cheat"))
            return new CheatMenuController().run(inputParts[1], inputParts[2]) + returnBoards(inputParts[inputParts.length - 1]);

        return "break";
    }

    private static String returnBoards(String token) {
        Game game = Game.getGameByToken(token);
        assert game != null;
        return "\n" + game.getBoard1().turnToJson() + "\n" + game.getBoard2().turnToJson();
    }

    private static User getLoggedInUserByToken(String token) {
        for (User user : LoginMenuController.loggedInUsers) {
            if (user.getToken().equals(token)) {
                return user;
            }
        }
        return null;
    }
}
