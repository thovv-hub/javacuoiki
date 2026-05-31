package org.example.cuoikijava.dao;

import org.example.cuoikijava.model.Bus;
import org.example.cuoikijava.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class BusDAO {

    public void addBus(Bus bus) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(bus);
        transaction.commit();
        session.close();
    }

    public void updateBus(Bus bus) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.merge(bus);
        transaction.commit();
        session.close();
    }

    public void deleteBus(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Bus bus = session.find(Bus.class, id);

        if(bus != null) {
            session.remove(bus);
        }
        transaction.commit();
        session.close();
    }

    public List<Bus> getAllBus() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Bus> buses = session.createQuery("FROM Bus", Bus.class).list();
        session.close();
        return buses;
    }

    public Bus getBusByPlate(String plate) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Bus bus = session.createQuery("FROM Bus WHERE licensePlate = :plate", Bus.class)
                .setParameter("plate", plate).uniqueResult();
        session.close();
        return bus;
    }
}