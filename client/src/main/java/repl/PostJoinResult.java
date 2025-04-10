package repl;

public class PostJoinResult {

    public ResultType result;
    public GameInfo gameInfo;

    public PostJoinResult(ResultType result) {
        this.result = result;
    }

    public PostJoinResult(GameInfo game) {
        this.result = ResultType.JOIN;
        this.gameInfo = game;
    }

    public enum ResultType {
        QUIT,
        LOGOUT,
        JOIN
    }

    public record GameInfo(int gameID, String authToken, String teamColor) {};
}
