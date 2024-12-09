package dao;

import modal.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import hibernate.HibernateUtil;
import modal.Meal;

import java.util.List;
import modal.MealType;
import org.hibernate.Query;

public class MealDao {

    private SessionFactory sessionFactory;

    // Constructor to initialize SessionFactory
    public MealDao() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public boolean createMeal(Meal meal) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Validate meal data before saving
            if (meal.getType() == null) {
                throw new IllegalArgumentException("Meal type cannot be null");
            }
            if (meal.getAmount() <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }

            // If no ID is provided, this will create a new meal
            session.saveOrUpdate(meal);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to create/update meal: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    public boolean updateMeal(Meal meal) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Validate meal data before updating
            if (meal.getType() == null) {
                throw new IllegalArgumentException("Meal type cannot be null");
            }
            if (meal.getAmount() <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }

            // Use merge instead of update or saveOrUpdate
            session.merge(meal);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    public List<Meal> getAllMeals() {
        Session session = sessionFactory.openSession();
        String hql = "FROM Meal";
        Query query = session.createQuery(hql);
        List<Meal> foods = query.list();
        session.close();
        return foods;
    }

    // Get food by ID
    public Meal getMealById(int mealId) {
        Session session = sessionFactory.openSession();
        Meal meal = (Meal) session.get(Meal.class, mealId);
        session.close();
        return meal;
    }

    public boolean deleteMeal(int mealId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            Meal food = (Meal) session.get(Meal.class, mealId);
            if (food != null) {
                session.delete(food);
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
                return false;
            }
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }
    
    
    // Get food by Amount
    // Retrieve Meal by Amount using Meal as Parameter
public Meal getMealByAmount(Meal meal) {
    if (meal == null || meal.getAmount() < 0) {
        throw new IllegalArgumentException("Invalid Meal object. Amount must be positive.");
    }

    Session session = sessionFactory.openSession();
    try {
        String hql = "FROM Meal WHERE amount = :mealAmount";
        Query query = session.createQuery(hql);
        query.setParameter("mealAmount", meal.getAmount()); // Use the amount from the Meal object

        // Get the result. Assume there is at most one meal with the given amount.
        Meal result = (Meal) query.uniqueResult();
        return result;
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Failed to fetch meal by amount: " + e.getMessage());
    } finally {
        session.close();
    }
}



// Retrieve Meals by MealType
public List<Meal> getMealsByType(MealType mealType) {
    if (mealType == null) {
        throw new IllegalArgumentException("MealType cannot be null.");
    }

    Session session = sessionFactory.openSession();
    try {
        String hql = "FROM Meal WHERE type = :mealType";
        Query query = session.createQuery(hql);
        query.setParameter("mealType", mealType);

        // Get the list of meals matching the specified type
        List<Meal> meals = query.list();
        return meals;
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Failed to fetch meals by type: " + e.getMessage());
    } finally {
        session.close();
    }
}

}
