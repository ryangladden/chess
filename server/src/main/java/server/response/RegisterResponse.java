package server.response;

import model.AuthData;

public record RegisterResponse(int status, AuthData authData) {
}
