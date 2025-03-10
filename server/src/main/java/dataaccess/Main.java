package dataaccess;

import model.UserData;
import server.Server;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseDataAccess dataAccess = new DatabaseDataAccess();
            dataAccess.createUser(new UserData("joemama", "password", "joemama@gmail.com"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
