package modal;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "schedule_food")
public class ScheduleFood implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(name = "amount", nullable = false)
    private float amount;

    public ScheduleFood() {}

    public ScheduleFood(Food food, float amount) {
        this.food = food;
        this.amount = amount;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Food getFood() { return food; }
    public void setFood(Food food) { this.food = food; }

    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }

    @Override
    public String toString() {
        return "ScheduleFood{" + "id=" + id + ", food=" + food + ", amount=" + amount + '}';
    }
    
    
}