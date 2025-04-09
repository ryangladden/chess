package client;

import websocket.WebsocketFacade;

public class GameClient {

    private final WebsocketFacade websocket;
    private final String authToken;
    private final int gameID;

    public GameClient(WebsocketFacade websocket, String authToken, int gameID) {
        this.websocket = websocket;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public String eval(String input) {
        String[] command = input.split(" ");
        return switch(command[0]) {
            case "leave" -> leave();
            case "resign" -> resign();
            case "move" -> move(command);
//            case "help" -> "help";
            case "show" -> show(command);
            case "board" -> printBoard();
            default -> "help";
        };
    }

    private String leave() {
        return null;
    }

    private String resign() {
        return null;
    }

    private String move(String[] command) {
        return null;
    }

    private String show(String[] command) {
        return null;
    }

    private String printBoard() {
        return null;
    }
}
