package server.response;
import dataaccess.GameData;
import java.util.Collection;

public record ListGameResponse(Collection<GameData> games) {
}
