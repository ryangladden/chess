import model.AuthData;
import repl.PostJoinRepl;
import repl.PreJoinRepl;
import server.ServerFacade;

public class Main {
    static final ServerFacade SERVER = new ServerFacade(8080);

    public static void main(String[] args) {
        boolean cont = true;
        while (cont) {
            PreJoinRepl preJoin = new PreJoinRepl(SERVER);
            AuthData auth = preJoin.run();
            if (auth != null) {
                PostJoinRepl postJoin = new PostJoinRepl(SERVER, auth);
                cont = postJoin.run();
            } else {
                break;
            }
        }
    }
}