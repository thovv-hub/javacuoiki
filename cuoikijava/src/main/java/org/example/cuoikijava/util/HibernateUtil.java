package org.example.cuoikijava.util;

import org.example.cuoikijava.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {

        try {
            return new Configuration().configure("hibernate.cfg.xml")
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Bus.class)
                    .addAnnotatedClass(Trip.class)
                    .addAnnotatedClass(Ticket.class)
                    .addAnnotatedClass(Seat.class)
                    .addAnnotatedClass(Message.class)
                    .buildSessionFactory();
        }

        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Hibernate Error!");
        }
    }

    public static SessionFactory getSessionFactory() {

        return sessionFactory;
    }
}