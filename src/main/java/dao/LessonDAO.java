package dao;

import config.HibernateSessionFactoryUtil;
import models.Auds;
import models.Lesson;
import models.Students;
import models.Teachers;
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
                session.createCriteria(Lesson.class)
                        .add(Restrictions.eq("groupp", groupp))
                        .list();
        return lessons;
    }

    public List<Lesson> findBySubgroup(String subgroupp) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Lesson> lessons =
                session.createCriteria(Lesson.class)
                        .add(Restrictions.eq("subgroup", subgroupp))
                        .list();
        return lessons;
    }

    public List<Lesson> findByTeacherID(Teachers teacher) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Lesson> lessons =
                session.createCriteria(Lesson.class)
                        .add(Restrictions.eq("teacher", teacher))
                        .list();
        return lessons;
    }

    public List<Lesson> findByAudID(Auds aud, String numberOfWeek, String numberOfDay, String numberOfClass) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Lesson> lessons =
                session.createCriteria(Lesson.class)
                        .add(Restrictions.eq("aud", aud))
                        .add(Restrictions.eq("numberOfWeek", numberOfWeek))
                        .add(Restrictions.eq("numberOfDay", numberOfDay))
                        .add(Restrictions.eq("numberOfClass", numberOfClass))
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
