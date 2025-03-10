package dataaccess;

import model.AuthData;
import model.UserData;
import server.Server;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseDataAccess dataAccess = new DatabaseDataAccess();
            dataAccess.createAuth(new AuthData(UUID.randomUUID().toString(), "joemama"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
