package modal;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "MealSchedule")
public class MealSchedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Many-to-One relationship with User
    @ManyToOne(fetch = FetchType.EAGER) // Fetch user eagerly with the meal schedule
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column in MealSchedule
    private User user;

    // Enum for meal type
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    // Many-to-Many relationship with Food
    @ManyToMany(fetch = FetchType.EAGER) // Fetch related foods eagerly
    @JoinTable(
        name = "MealSchedule_Food", // Join table name
        joinColumns = @JoinColumn(name = "schedule_id"), // Link to MealSchedule
        inverseJoinColumns = @JoinColumn(name = "food_id") // Link to Food
    )
    private List<Food> foods;

    @Column(name = "total_amount", nullable = false)
    private float totalAmount;

    @Column(name = "meal_amount", nullable = false)
    private float mealAmount;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    // Default constructor
    public MealSchedule() {
        // Set the creation date automatically
        this.creationDate = LocalDateTime.now();
    }

    // Parameterized constructor for convenience
    public MealSchedule(int id, User user, MealType mealType, List<Food> foods, float totalAmount, float mealAmount) {
        this.id = id;
        this.user = user;
        this.mealType = mealType;
        this.foods = foods;
        this.totalAmount = totalAmount;
        this.mealAmount = mealAmount;
        this.creationDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public float getMealAmount() {
        return mealAmount;
    }

    public void setMealAmount(float mealAmount) {
        this.mealAmount = mealAmount;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    // Calculate total food amount
    
      private float calculateTotalAmount() {
        return (float) foods.stream().mapToDouble(food -> food.getAmmount()).sum();
    }
    // Validate that total amount does not exceed meal amount
    public boolean isValidMealSchedule() {
        return this.totalAmount <= this.mealAmount;
    }

    @Override
    public String toString() {
        return "MealSchedule{" +
                "id=" + id +
                ", user=" + user +
                ", mealType=" + mealType +
                ", foods=" + foods +
                ", totalAmount=" + totalAmount +
                ", mealAmount=" + mealAmount +
                ", creationDate=" + creationDate +
                '}';
    }
}





  

