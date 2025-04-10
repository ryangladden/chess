package websocket;

import repl.GameRepl;
import server.ServerFacade;

public class Main {

    public static void main(String[] args) {
        GameRepl repl = new GameRepl(new ServerFacade(8080), "70585341-e833-4b5b-96b5-b4ec31fde829", 1, "observer");
        repl.run();
    }
}