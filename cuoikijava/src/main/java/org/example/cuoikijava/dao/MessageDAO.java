package org.example.cuoikijava.dao;

import org.example.cuoikijava.model.Message;
import org.example.cuoikijava.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class MessageDAO {
    public void sendMessage(Message message) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(message);
        transaction.commit();
        session.close();
    }

    public List<Message> getAllMessage() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Message> messages = session.createQuery("FROM Message", Message.class).list();
        session.close();
        return messages;
    }
}