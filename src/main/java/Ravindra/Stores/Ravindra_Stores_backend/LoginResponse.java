package Ravindra.Stores.Ravindra_Stores_backend;

public class LoginResponse {
        private String username;
        private String role;
        private String jwt;
        private String picture;

    public LoginResponse(String username, String role, String jwt, String picture) {
        this.username = username;
        this.role = role;
        this.jwt = jwt;
        this.picture = picture;
    }

    
    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJwt() {
        return jwt;
    }

    public String getPicture() {
        return picture;
    }
}
