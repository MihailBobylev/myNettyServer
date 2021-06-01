package dao;

import config.HibernateSessionFactoryUtil;
import models.Lesson;
import models.LessonBySubgroup;
import models.Students;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class LessonBySubgroupDAO {
    public LessonBySubgroup findLessonById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(LessonBySubgroup.class, id);
    }

    public List<LessonBySubgroup> findByLessonId(int id_lesson) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<LessonBySubgroup> lessons =
                session.createCriteria(LessonBySubgroup.class)
                        .add(Restrictions.eq("id_lesson", id_lesson))
                        .list();
        return lessons;
    }

    public List<LessonBySubgroup> findByStudentId(int id_students) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<LessonBySubgroup> students =
                session.createCriteria(LessonBySubgroup.class)
                        .add(Restrictions.eq("id_students", id_students))
                        .list();
        return students;
    }

    public void save(LessonBySubgroup lessonBySubgroup) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(lessonBySubgroup);
        tx1.commit();
        session.close();
    }

    public void update(LessonBySubgroup lessonBySubgroup) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(lessonBySubgroup);
        tx1.commit();
        session.close();
    }

    public void delete(LessonBySubgroup lessonBySubgroup) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(lessonBySubgroup);
        tx1.commit();
        session.close();
    }
    public void deleteAll() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.createQuery("DELETE FROM lessonbysubgroup").executeUpdate();
        tx1.commit();
        session.close();
    }
    public List<LessonBySubgroup> findAll() {
        List<LessonBySubgroup> lessons = (List<LessonBySubgroup>) HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From LessonBySubgroup").list();
        return lessons;
    }
}
