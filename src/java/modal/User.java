package modal;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author princ
 */

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private String email;
    private String phone;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Constructors, Getters, and Setters

    public User() {
    }

    public User(int userId, String email, String phone, String password, Role role) {
        this.userId = userId;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public User(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", email=" + email + ", phone=" + phone + ", password=" + password + ", role=" + role + '}';
    }

    


    
}
