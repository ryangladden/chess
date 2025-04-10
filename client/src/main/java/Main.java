import model.AuthData;
import repl.GameRepl;
import repl.PostJoinRepl;
import repl.PostJoinResult;
import repl.PreJoinRepl;
import server.ServerFacade;
import websocket.WebsocketFacade;

public class Main {
    static final ServerFacade SERVER = new ServerFacade(8080);

    public static void main(String[] args) {
        boolean cont = true;
        AuthData auth = null;
        while (cont) {
            if (auth != null) {
                PostJoinRepl postJoin = new PostJoinRepl(SERVER, auth);
                PostJoinResult result = postJoin.run();
                if (result.result == PostJoinResult.ResultType.JOIN) {
                    GameRepl game = new GameRepl(SERVER, result.gameInfo.authToken(), result.gameInfo.gameID(), result.gameInfo.teamColor());
                    game.run();
                } else if (result.result == PostJoinResult.ResultType.LOGOUT) {
                    auth = null;
                } else if (result.result == PostJoinResult.ResultType.QUIT) {
                    cont = false;
                }
                }
            if (auth==null) {
                PreJoinRepl preJoin = new PreJoinRepl(SERVER);
                auth = preJoin.run();
            }
        }
    }
}