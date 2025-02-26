package server.response;
import dataaccess.AuthData;

public record LoginResponse(int status, AuthData authData){
}
