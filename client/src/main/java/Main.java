import chess.*;
import client.PostJoinClient;
import model.AuthData;
import model.GameData;
import model.UserData;
import repl.PostJoinRepl;
import repl.PreJoinRepl;
import server.ServerFacade;

public class Main {
    static final ServerFacade server = new ServerFacade("http://localhost:8080");
    public static void main(String[] args) {
        boolean cont = true;
        while(cont) {
            PreJoinRepl preJoin = new PreJoinRepl(server);
            AuthData auth = preJoin.run();
            if (auth != null) {
                PostJoinRepl postJoin = new PostJoinRepl(server, auth);
                cont = postJoin.run();
            }
            else {
                break;
            }
        }
    }
}