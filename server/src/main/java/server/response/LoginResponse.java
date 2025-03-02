package server.response;

import model.AuthData;

public record LoginResponse(int status, AuthData authData) {
}
