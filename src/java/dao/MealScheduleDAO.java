package dao;

import hibernate.HibernateUtil;
import modal.MealSchedule;
import modal.User;
import org.hibernate.*;
import org.hibernate.Transaction;
import org.hibernate.Query;

import java.util.List;

public class MealScheduleDAO {

    Session session = HibernateUtil.getSessionFactory().openSession();

    public MealSchedule createSchedule(MealSchedule schedule) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction(); // Begin transaction

            // Save the schedule object
            session.save(schedule);

            transaction.commit(); // Commit the transaction to persist changes
            return schedule; // Return the saved object
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Rollback transaction in case of error
            }
            throw new RuntimeException("Error while creating meal schedule", e);
        }
    }

    public MealSchedule getScheduleById(int id) {
        try {
            return (MealSchedule) session.get(MealSchedule.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving meal schedule by ID", e);
        }
    }

    public List<MealSchedule> getAllSchedules() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
//        String hql = "FROM MealSchedule";
            String hql = "FROM MealSchedule ms LEFT JOIN FETCH ms.foods LEFT JOIN FETCH ms.user";
            Query query = session.createQuery(hql);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving meal schedules", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public MealSchedule updateSchedule(MealSchedule schedule) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            if (schedule.isValidMealSchedule()) {
                session.update(schedule);
            } else {
                throw new IllegalArgumentException("The total food amount exceeds the meal amount.");
            }

            transaction.commit();
            return schedule;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error while updating meal schedule", e);
        }
    }

    public void deleteSchedule(int id) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            MealSchedule schedule = (MealSchedule) session.get(MealSchedule.class, id);

            if (schedule != null) {
                session.delete(schedule);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error while deleting meal schedule", e);
        }
    }

public List<MealSchedule> getSchedulesByUser(User user) {
    Session session = null;
    try {
        session = HibernateUtil.getSessionFactory().openSession();
        String hql = "FROM MealSchedule ms WHERE ms.user = :user";
        Query query = session.createQuery(hql);
        query.setParameter("user", user);  // Add this line to set the user parameter
        return query.list();
    } catch (Exception e) {
        throw new RuntimeException("Error retrieving meal schedules", e);
    } finally {
        if (session != null) {
            session.close();
        }
    }
}

}
