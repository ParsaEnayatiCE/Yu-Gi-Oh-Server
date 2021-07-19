package controller.menucontroller;


import view.StatusEnum;
import models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

//-----------------------------------PLEASE LOGIN FIRST NOT FIXED-------------------

public class LoginMenuController {
    public static ArrayList<User> loggedInUsers = new ArrayList<>();

    private boolean doesUserExist(String username) {
        for (User user : User.allUsers) {
            if (user.getUserName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPasswordCorrect(String username, String password) {
        return Objects.requireNonNull(User.getUserByUserName(username)).getPassword().equals(password);
    }

    public synchronized String loginUSer(String username, String password) {
        if (!doesUserExist(username))
            return "There is no user with username " + username;

        if (!isPasswordCorrect(username, password))
            return StatusEnum.USERNAME_AND_PASSWORD_MISMATCH.getStatus();


        for (User u: User.allUsers
             ) {
            if(u.getUserName().equals(username)&&u.getPassword().equals(password)){
                u.setToken(UUID.randomUUID().toString());
                loggedInUsers.add(u);
                return u.getToken();
            }
        }
        return "";

    }

    public synchronized String createUser(String username, String nickname, String password) {
        if (User.isUserNameTaken(username))
            return "user with username " + username + " already exists";

        if (User.isNickNameTaken(nickname))
            return "user with nickname " + nickname + " already exists";

        new User(username, nickname, password);
        return StatusEnum.USER_CREATE_SUCCESSFULLY.getStatus();
    }

    public synchronized String logout(String token,String username){
        for (int i = 0; i < loggedInUsers.size(); i++) {
            if (loggedInUsers.get(i).getToken().equals(token) && loggedInUsers.get(i).getUserName().equals(username)){
                loggedInUsers.remove(i);
                return StatusEnum.USER_LOGOUT_SUCCESSFULLY.getStatus();
            }
        }
        return "User not found";
    }

}
