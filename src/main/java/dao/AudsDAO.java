package dao;

import config.HibernateSessionFactoryUtil;
import models.Area;
import models.Auds;
import models.Hallway;
import models.Students;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class AudsDAO {
    public Auds findAudsById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Auds.class, id);
    }

    public List<Auds> findByAud(String corp, String aud) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Auds> auds =
                session.createCriteria(Auds.class)
                        .add(Restrictions.eq("corp", corp))
                        .add(Restrictions.eq("number", aud))
                        .list();
        return auds;
    }

    public void save(Auds auds) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(auds);
        tx1.commit();
        session.close();
    }

    public void update(Auds auds) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(auds);
        tx1.commit();
        session.close();
    }

    public void delete(Auds auds) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(auds);
        tx1.commit();
        session.close();
    }

    public List<Auds> findAll() {
        List<Auds> auds = (List<Auds>) HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Auds").list();
        return auds;
    }
}
