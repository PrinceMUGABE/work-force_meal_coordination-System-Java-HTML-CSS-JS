package modal;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Represents a meal with type, amount, and ID.
 */
@Entity
@Table(name = "Meal")
public class Meal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MealType type;

    @Column(name = "amount", nullable = false)
    private float amount;

    public Meal() {
    }

    public Meal(int id) {
        this.id = id;
    }

    public Meal(int id, MealType type, float amount) {
        this.id = id;
        this.type = type;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MealType getType() {
        return type;
    }

    public void setType(MealType type) {
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Meal{" + "id=" + id + ", type=" + type + ", amount=" + amount + '}';
    }

    
    
}
