package dao;

import config.HibernateSessionFactoryUtil;
import models.Classroom;
import models.Hallway;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class HallwayDAO {
    public Hallway findHallwayById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Hallway.class, id);
    }

    public void save(Hallway hallway) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(hallway);
        tx1.commit();
        session.close();
    }

    public void update(Hallway hallway) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(hallway);
        tx1.commit();
        session.close();
    }

    public void delete(Hallway hallway) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(hallway);
        tx1.commit();
        session.close();
    }

    public Classroom findClassroomById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Classroom.class, id);
    }

    public List<Classroom> findClassroomByNumber(String number) { //String corp,
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Classroom> corps =
                session.createCriteria(Classroom.class)
                        .add(Restrictions.eq("number", number))
                        .list();
        return corps;
    }

    public List<Hallway> findAll() {
        List<Hallway> users = (List<Hallway>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Hallway").list();
        return users;
    }
}
