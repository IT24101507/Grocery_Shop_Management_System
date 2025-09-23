package Ravindra.Stores.Ravindra_Stores_backend;

public class LoginRequest {
    
    private String gmail;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String gmail, String password) {
        this.gmail = gmail;
        this.password = password;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
