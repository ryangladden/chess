package client;

import model.AuthData;
import server.ServerFacade;

public class PostJoinClient {

    private final ServerFacade server;
    private final AuthData auth;

    public PostJoinClient(ServerFacade server, AuthData auth) {
        this.server = server;
        this.auth = auth;
    }

    public String eval(String input) {
        String[] command = input.toLowerCase().split(" ");
//        return switch(command[0]) {
//            case "create" -> createGame(command);
//            case "list" -> listGames();
//            case "join" -> joinGame(command);
//            case "quit" -> quit();
//            default -> help();
//        };
        return null;
    }

    private String createGame(String[] command) {
        return "Create game";
    }

    private String listGames() {
        return "List games";
    }

    private String joinGame(String[] command ) {
        return "Join game";
    }

    private String quit() {
        return "quit";
    }

    public String help() {
        return "Help";
    }
}
