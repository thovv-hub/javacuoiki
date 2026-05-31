package org.example.cuoikijava.dao;

import org.example.cuoikijava.model.Ticket;
import org.example.cuoikijava.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class TicketDAO {

    public void bookTicket(Ticket ticket) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(ticket);
        transaction.commit();
        session.close();
    }

    public void deleteTicket(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Ticket ticket = session.find(Ticket.class, id);

        if(ticket != null) {
            session.remove(ticket);
        }
        transaction.commit();
        session.close();
    }

    public List<Ticket> getAllTicket() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Ticket> tickets = session.createQuery("FROM Ticket", Ticket.class).list();
        session.close();
        return tickets;
    }

    public List<Ticket> getTicketByCustomer(String customerName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Ticket> tickets = session.createQuery("FROM Ticket WHERE customerName = :name", Ticket.class)
                        .setParameter("name", customerName).list();
        session.close();
        return tickets;
    }

    public List<Ticket> getTicketByBus(String plate) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Ticket> tickets = session.createQuery("FROM Ticket WHERE licensePlate = :plate", Ticket.class)
                .setParameter("plate", plate).list();
        session.close();
        return tickets;
    }

    public boolean isSeatBooked(String plate, int seat) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Long count = session.createQuery("""
                                SELECT COUNT(t)
                                FROM Ticket t
                                WHERE t.licensePlate = :plate
                                AND t.seatNumber = :seat
                                """,
                                Long.class)
                .setParameter("plate", plate).setParameter("seat", seat).uniqueResult();
        session.close();
        return count != null && count > 0;
    }

    public List<Ticket> getTicketByTrip(String tripName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Ticket> tickets = session.createQuery("FROM Ticket WHERE trip_name = :trip", Ticket.class)
                .setParameter("trip", tripName).list();session.close();
        return tickets;
    }

    public List<Integer> getBookedSeats(String plate) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Integer> seats = session.createQuery(
                                """
                                SELECT t.seatNumber
                                FROM Ticket t
                                WHERE t.licensePlate = :plate
                                """,

                                Integer.class)
                .setParameter("plate", plate).list();

        session.close();
        return seats;
    }

}