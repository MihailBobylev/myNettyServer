package dao;

import config.HibernateSessionFactoryUtil;
import models.Students;
import models.Teachers;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class TeachersDAO {
    public Teachers findTeacherById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Teachers.class, id);
    }

    public List<Teachers> findByTeacher(String name) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Teachers> teachers =
                session.createCriteria(Teachers.class)
                        .add(Restrictions.eq("name", name))
                        .list();
        return teachers;
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

    public void deleteAll() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.createQuery("DELETE FROM Teachers").executeUpdate();
        tx1.commit();
        session.close();
    }

    public List<Teachers> findAll() {
        List<Teachers> teachers = (List<Teachers>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Teachers").list();
        return teachers;
    }
}
