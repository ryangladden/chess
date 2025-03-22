import model.AuthData;
import model.UserData;
import server.ServerFacade;

public class PreJoinClient {


    public AuthData auth;
    private final String serverUrl;
    private final ServerFacade server;

    public PreJoinClient(String url) {
        this.serverUrl = url;
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        String[] command = input.toLowerCase().split(" ");
//        return switch(command[0]) {
//            case "login" -> login(command);
//            case "register" -> register(command);
//            case "help" -> help();
//            case "quit" -> quit();
//        };
        return null;
    }

    private String login(String[] command) {
        if (command.length >= 3) {
            auth = server.login(new UserData(command[1], command[2], null));

        }
        return null;
    }
}
