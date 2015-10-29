package com.vall.vall;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class DataHolder {

    public static ArrayList<QBUser> usersList;
    public static final String PASSWORD = "vall@123$";

    public static ArrayList<QBUser> getUsersList() {

        if (usersList == null) {
            usersList = new ArrayList<>();

            QBUser user = new QBUser("8050888642", PASSWORD);
            user.setId(6485549);
            user.setFullName("Sunu");
            usersList.add(user);
            //
            user = new QBUser("7795200172", PASSWORD);
            user.setId(6485619);
            user.setFullName("Ash");
            usersList.add(user);
            //
            user = new QBUser("9742381630", PASSWORD);
            user.setId(6493232);
            user.setFullName("Karthik");
            usersList.add(user);
        }

        return usersList;
    }


    public static String getUserNameByID(Integer callerID) {
        for (QBUser user : getUsersList()) {
            if (user.getId().equals(callerID)) {
                return user.getFullName();
            }
        }
        return "User_name_unused";
    }

    public static String getUserNameByLogin(String login) {
        for (QBUser user : getUsersList()) {
            if (user.getLogin().equals(login)) {
                return user.getFullName();
            }
        }
        return "User_name_unused";
    }

    public static int getUserIndexByID(Integer callerID) {
        for (QBUser user : getUsersList()) {
            if (user.getId().equals(callerID)) {
                return usersList.indexOf(user);
            }
        }
        return -1;
    }

    public static ArrayList<Integer> getIdsAiiUsers (){
        ArrayList<Integer> ids = new ArrayList<>();
        for (QBUser user : getUsersList()){
            ids.add(user.getId());
        }
        return ids;
    }
}