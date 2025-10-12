package Ravindra.Stores.Ravindra_Stores_backend;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table (name = "Customers")
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String gmail;
    private String password;
    private String role;
    private String telephone;
    private String fullName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postalCode;
    private boolean verified;
    private boolean enabled;

    @Column(nullable = true)
    private String picture;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private VerificationToken verificationToken;

    public User(){}

    public User(String username, String gmail, String password, String role, String telephone, String fullName, String addressLine1, String addressLine2, String city, String postalCode){
        this.username = username;
        this.gmail = gmail;
        this.password = password;
        this.role = role;
        this.telephone = telephone;
        this.fullName = fullName;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
    }

    public long getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getGmail(){
        return gmail;
    }

    public void setGmail(String gmail){
        this.gmail = gmail;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password= password;
    }

    public String getRole(){
        return role;
    }

    public void setRole(String role){
        this.role = role;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone){
        this.telephone = telephone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public boolean isVerified() {
        return verified;
    }

        public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public VerificationToken getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(VerificationToken verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + "'" +
                ", gmail='" + gmail + "'" +
                ", password='" + password + "'" +
                ", role='" + role + "'" +
                ", telephone='" + telephone + "'" +
                ", fullName='" + fullName + "'" +
                ", addressLine1='" + addressLine1 + "'" +
                ", addressLine2='" + addressLine2 + "'" +
                ", city='" + city + "'" +
                ", postalCode='" + postalCode + "'" +
                ", picture='" + picture + "'" + 
                ", verified=" + verified +
                ", enabled=" + enabled +
                '}';
    }

}
