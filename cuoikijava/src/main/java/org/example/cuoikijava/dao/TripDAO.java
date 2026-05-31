package org.example.cuoikijava.dao;

import org.example.cuoikijava.model.Trip;
import org.example.cuoikijava.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class TripDAO {

    public void addTrip(Trip trip) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(trip);
        transaction.commit();
        session.close();
    }

    public void updateTrip(Trip trip) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.merge(trip);
        transaction.commit();
        session.close();
    }

    public void deleteTrip(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Trip trip = session.find(Trip.class, id);
        if(trip != null) {
            session.remove(trip);
        }
        transaction.commit();
        session.close();
    }

    public List<Trip> getAllTrip() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Trip> trips = session.createQuery("FROM Trip", Trip.class).list();
        session.close();
        return trips;
    }

    public Trip getTripById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Trip trip = session.find(Trip.class, id);
        session.close();
        return trip;
    }

    public List<Trip> getTripByBus(String plate) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Trip> trips = session.createQuery("FROM Trip WHERE bus_license_plate = :plate", Trip.class)
                .setParameter("plate", plate).list();

        session.close();
        return trips;
    }

}