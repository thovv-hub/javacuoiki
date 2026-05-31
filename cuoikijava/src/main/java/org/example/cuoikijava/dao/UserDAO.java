package org.example.cuoikijava.dao;

import org.example.cuoikijava.model.User;
import org.example.cuoikijava.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserDAO {

    public User login(String username, String password) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        User user = session.createQuery("FROM User WHERE username = :u AND password = :p", User.class)
                .setParameter("u", username).setParameter("p", password).uniqueResult();
        session.close();
        return user;
    }

    public List<User> getAllUser() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<User> users = session.createQuery("FROM User", User.class).list();
        session.close();
        return users;
    }

    public User getUserByUsername(String username) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        User user = session.createQuery("FROM User WHERE username = :u", User.class)
                .setParameter("u", username).uniqueResult();

        session.close();
        return user;
    }

    public boolean usernameExists(String username) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Long count = session.createQuery(
                                """
                                SELECT COUNT(u)
                                FROM User u
                                WHERE u.username = :username
                                """,

                                Long.class)
                .setParameter("username", username).uniqueResult();
        session.close();

        return count != null && count > 0;
    }

    public void register(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(user);
        transaction.commit();
        session.close();
    }

    public void updateUser(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.merge(user);
        transaction.commit();
        session.close();
    }
    public void deleteUser(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        User user = session.find(User.class, id);

        if(user != null) {
            session.remove(user);
        }
        transaction.commit();
        session.close();
    }



}