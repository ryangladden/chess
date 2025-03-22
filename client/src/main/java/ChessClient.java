import server.ServerFacade;

public class ChessClient {

    private String username;
    private String authToken;
    private final String serverUrl;
    private final ServerFacade server;


    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
    }

    public String preJoinEval(String input) {
        String[] command = input.toLowerCase().split(" ");
//        return switch(command[0]) {
//            case "login" -> login(command);
//            case "register" -> register(command);
//            case "quit" -> quit();
//            default -> help();
//        };
        return null;
    }
}
