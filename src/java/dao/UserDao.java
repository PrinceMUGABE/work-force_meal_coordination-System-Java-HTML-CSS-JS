package dao;

import modal.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import hibernate.HibernateUtil;  // Use HibernateUtil for sessionFactory

import java.util.List;
import org.hibernate.Query;

public class UserDao {

    private SessionFactory sessionFactory;

    // Constructor to initialize SessionFactory
    public UserDao() {
        this.sessionFactory = HibernateUtil.getSessionFactory();  // Get session factory from HibernateUtil
    }

    // Create User
    public void createUser(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(user); // Hibernate save method to persist the user object
        transaction.commit();
        session.close();
    }

    // Get User by ID
    public User getUserById(int userId) {
        Session session = sessionFactory.openSession();
        User user = (User) session.get(User.class, userId); // Hibernate get method to retrieve user by ID
        session.close();
        return user;
    }

    // Get User by Email
    public User getUserByEmail(String email) {
        Session session = sessionFactory.openSession();
        String hql = "FROM User u WHERE u.email = :email";
        Query query = session.createQuery(hql);
        query.setParameter("email", email);
        User user = (User) query.uniqueResult(); // Cast the result to User
        session.close();
        return user;
    }

    // Get User by Phone
    public User getUserByPhone(String email) {
        Session session = sessionFactory.openSession();
        String hql = "FROM User u WHERE u.phone = :phone";
        Query query = session.createQuery(hql);
        query.setParameter("email", email);
        User user = (User) query.uniqueResult(); // Cast the result to User
        session.close();
        return user;
    }

    // Get All Users
    public List<User> getAllUsers() {
        Session session = sessionFactory.openSession();
        String hql = "FROM User";
        Query query = session.createQuery(hql); // No need to specify the entity class here
        List<User> users = query.list(); // Fetch all users
        session.close();
        return users;
    }

    // Update User by ID
    public boolean updateUser(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(user); // Hibernate update method to modify the user entity
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

    // Delete User by ID
    public boolean deleteUser(int userId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            User user = (User) session.get(User.class, userId); // Retrieve the User entity
            if (user != null) {
                session.delete(user); // Now delete the entity
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

    public boolean isEmailExists(String email) {
        Session session = sessionFactory.openSession();
        String hql = "SELECT COUNT(*) FROM User u WHERE u.email = :email";
        Query query = session.createQuery(hql);
        query.setParameter("email", email);
        long count = (Long) query.uniqueResult();
        session.close();
        return count > 0;
    }

    public boolean isPhoneExists(String phone) {
        Session session = sessionFactory.openSession();
        String hql = "SELECT COUNT(*) FROM User u WHERE u.phone = :phone";
        Query query = session.createQuery(hql);
        query.setParameter("phone", phone);
        long count = (Long) query.uniqueResult();
        session.close();
        return count > 0;
    }

}
