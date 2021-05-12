package dao;

import config.HibernateSessionFactoryUtil;
import models.Lesson;
import models.Students;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class LessonDAO {
    public Lesson findLessonById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Lesson.class, id);
    }

    public List<Lesson> findByGroup(String groupp) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Lesson> lessons =
                session.createCriteria(Students.class)
                        .add(Restrictions.eq("groupp", groupp))
                        .list();
        return lessons;
    }

    public void save(Lesson lesson) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(lesson);
        tx1.commit();
        session.close();
    }

    public void update(Lesson lesson) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(lesson);
        tx1.commit();
        session.close();
    }

    public void delete(Lesson lesson) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(lesson);
        tx1.commit();
        session.close();
    }
    public void deleteAll() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.createQuery("DELETE FROM Lesson").executeUpdate();
        tx1.commit();
        session.close();
    }
    public List<Lesson> findAll() {
        List<Lesson> lessons = (List<Lesson>) HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Lesson").list();
        return lessons;
    }
}
