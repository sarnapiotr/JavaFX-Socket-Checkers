package pl.edu.pk.checkers.common.message;

public class AuthData {
    private String username;
    private String password;

    public AuthData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
