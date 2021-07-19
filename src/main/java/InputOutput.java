import controller.menucontroller.LoginMenuController;
import controller.menucontroller.ProfileMenuController;
import models.User;
import view.StatusEnum;

import java.util.HashMap;
import java.util.UUID;

public class InputOutput {



    private static LoginMenuController loginMenuController = new LoginMenuController();
    public static String process(String input) {
        String[] inputParts = input.split("-");
        if(inputParts[0].equals("register")){
            String username = inputParts[1];
            String nickname = inputParts[2];
            String password = inputParts[3];
            return loginMenuController.createUser(username,nickname,password);
        }
        else if (inputParts[0].equals("login")){
            String username = inputParts[1];
            String password = inputParts[2];
            return loginMenuController.loginUSer(username,password);
        }
        else if (inputParts[0].equals("changeUsername")){
            String token = inputParts[inputParts.length-1];
            String newUserName = inputParts[1];
            if (getLoggedInUserByToken(token)!=null) {
                return new ProfileMenuController(User.getUserByUserName(token)).changeUsername(newUserName);
            }
            else{
                return "User is not logged in";
            }
        }
        else if (inputParts[0].equals("changePassword")){
            String token = inputParts[inputParts.length-1];
            String oldPass = inputParts[1];
            String newPass = inputParts[2];
            if (getLoggedInUserByToken(token)!=null) {
                return new ProfileMenuController(getLoggedInUserByToken(token)).changePass(oldPass,newPass);
            }
            else{
                return "User is not logged in";
            }

        }
        else if (inputParts[0].equals("changeNickname")){
            String token = inputParts[inputParts.length-1];
            String newNickname = inputParts[1];
            if (getLoggedInUserByToken(token)!=null) {
                return new ProfileMenuController(getLoggedInUserByToken(token)).changeNickname(newNickname);
            }
            else{
                return "User is not logged in";
            }
        }
        else if (inputParts[0].equals("logout")){
            String token = inputParts[inputParts.length-1];
            String username = inputParts[1];
            return loginMenuController.logout(token,username);
        }
        return "break";
    }

    private static User getLoggedInUserByToken(String token){
        for (User u: LoginMenuController.loggedInUsers
             ) {
            if (u.getToken().equals(token)){
                return u;
            }
        }
        return null;
    }
}
