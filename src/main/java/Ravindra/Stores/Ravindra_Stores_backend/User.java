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
    private String Address;
    private boolean verified;
    private boolean enabled;

    @Column(nullable = true)
    private String picture;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private VerificationToken verificationToken;

    public User(){}

    public User(String username, String gmail, String password, String role, String telephone, String Address){
        this.username = username;
        this.gmail = gmail;
        this.password = password;
        this.role = role;
        this.telephone = telephone;
        this.Address =  Address;
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

    public String getAddress(){
        return Address;
    }

    public void setAddress(String Address){
        this.Address = Address;
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
                ", picture='" + picture + "'" + 
                ", verified=" + verified +
                ", enabled=" + enabled +
                '}';
    }

}