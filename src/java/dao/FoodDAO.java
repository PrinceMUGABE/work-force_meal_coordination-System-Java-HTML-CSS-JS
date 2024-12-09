package dao;

import modal.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import hibernate.HibernateUtil;  // Use HibernateUtil for sessionFactory
import modal.Food;

import java.util.List;
import org.hibernate.Query;

public class FoodDAO {

    private SessionFactory sessionFactory;

    // Constructor to initialize SessionFactory
    public FoodDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();  // Get session factory from HibernateUtil
    }

    public void createFood(Food user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(user); // Hibernate save method to persist the user object
        transaction.commit();
        session.close();
    }

    public List<Food> getAllFood() {
        Session session = sessionFactory.openSession();
        String hql = "FROM Food";
        Query query = session.createQuery(hql); // No need to specify the entity class here
        List<Food> foods = query.list(); // Fetch all foods
        session.close();
        return foods;
    }

    // Get food by ID
    public Food getFoodbyId(int foodId) {
        Session session = sessionFactory.openSession();
        Food user = (Food) session.get(Food.class, foodId); // Hibernate get method to retrieve user by ID
        session.close();
        return user;
    }

    public boolean updateFood(Food food) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(food); // Hibernate update method to modify the food entity
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    public boolean deleteFood(int foodId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            Food food = (Food) session.get(Food.class, foodId); // Retrieve the User entity
            if (food != null) {
                session.delete(food); // Now delete the entity
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

    // Additional method to get foods by user
    public List<Food> getFoodsByUser(User user) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Food WHERE createdBy = :user";
            Query query = session.createQuery(hql);  // Only pass HQL string
            query.setParameter("user", user);  // Bind the user parameter
            return query.list();  // Use list() method for older versions of Hibernate
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

}
