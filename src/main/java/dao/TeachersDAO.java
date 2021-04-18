package dao;

import config.HibernateSessionFactoryUtil;
import models.Students;
import models.Teachers;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class TeachersDAO {
    public Teachers findTeacherById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Teachers.class, id);
    }

    public void save(Teachers teacher) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(teacher);
        tx1.commit();
        session.close();
    }

    public void update(Teachers teacher) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(teacher);
        tx1.commit();
        session.close();
    }

    public void delete(Teachers teacher) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(teacher);
        tx1.commit();
        session.close();
    }

    public List<Teachers> findAll() {
        List<Teachers> teachers = (List<Teachers>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From teachers").list();
        return teachers;
    }
}
