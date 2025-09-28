package Ravindra.Stores.Ravindra_Stores_backend;

public class LoginResponse {
    private String userNickname;  // For display
    private String gmail;         // For functionality
    private String role;
    private String jwt;
    private String picture;
    private String fullName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postalCode;
    private String telephone;

    public LoginResponse(String userNickname, String gmail, String role, String jwt, String picture, String fullName, String addressLine1, String addressLine2, String city, String postalCode, String telephone) {
        this.userNickname = userNickname;
        this.gmail = gmail;
        this.role = role;
        this.jwt = jwt;
        this.picture = picture;
        this.fullName = fullName;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
        this.telephone = telephone;
    }

    // Getters
    public String getUserNickname() {
        return userNickname;
    }

    public String getGmail() {
        return gmail;
    }

    public String getRole() {
        return role;
    }

    public String getJwt() {
        return jwt;
    }

    public String getPicture() {
        return picture;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getTelephone() {
        return telephone;
    }

    // Setters
    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}