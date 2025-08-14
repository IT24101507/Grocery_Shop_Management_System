package Ravindra.Stores.Ravindra_Stores_backend;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    public User(){}

    public User(String username, String gmail, String password, String role){
        this.username = username;
        this.gmail = gmail;
        this.password = password;
        this.role = role;
    }

    public long getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public void serUsername(String username){
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
}