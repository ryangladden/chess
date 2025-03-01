package server.response;

import dataaccess.AuthData;

public record RegisterResponse(int status, AuthData authData) {
}
