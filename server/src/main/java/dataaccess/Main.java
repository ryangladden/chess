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
            DataAccess dataAccess = new DatabaseDataAccess();
            UserData user = new UserData("joemama2", "password", "joemama@gmail.com");
            dataAccess.createUser(user);
            dataAccess.createNewGame("gamey wamey");
            Collection<GameData> games = dataAccess.listGames();
            for (GameData game : games) {
                System.out.println(game);
            }
            dataAccess.joinGame(user, 33, "BLACK");
            dataAccess.listGames();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
