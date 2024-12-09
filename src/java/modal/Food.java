package modal;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "Food")
public class Food implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // Add a field to store food image in binary format (byte array)
    @Lob
    @Column(name = "image")
    private byte[] image;
    
    private float ammount;

    public Food() {
    }

    public Food(int id) {
        this.id = id;
    }

    public Food(int id, String name, User createdBy, byte[] image, float ammount) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.image = image;
        this.ammount = ammount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public float getAmmount() {
        return ammount;
    }

    public void setAmmount(float ammount) {
        this.ammount = ammount;
    }

    @Override
    public String toString() {
        return "Food{" + "id=" + id + ", name=" + name + ", createdBy=" + createdBy + ", image=" + image + ", ammount=" + ammount + '}';
    }

    
    
    
}
