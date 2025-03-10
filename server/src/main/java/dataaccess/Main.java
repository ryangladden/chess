package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import server.Server;

import java.util.Collection;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseDataAccess dataAccess = new DatabaseDataAccess();
            dataAccess.createAuth(new AuthData(UUID.randomUUID().toString(), "joemama"));
            dataAccess.createNewGame("gamey wamey");
            Collection<GameData> games = dataAccess.listGames();
            for (GameData game : games) {
                System.out.println(game);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
