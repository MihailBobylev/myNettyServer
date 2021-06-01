package dao;

import config.HibernateSessionFactoryUtil;
import models.Students;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class StudentsDAO {
    public Students findStudentById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Students.class, id);
    }

    public List<Students> findByGroup(String groupp) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Students> students =
                session.createCriteria(Students.class)
                        .add(Restrictions.eq("groupp", groupp))
                        .list();
        return students;
    }

    public List<Students> findBySubgroup(String subGroupp) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Students> students =
                session.createCriteria(Students.class)
                        .add(Restrictions.eq("subgroup", subGroupp))
                        .list();
        return students;
    }

    public void save(Students student) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(student);
        tx1.commit();
        session.close();
    }

    public void update(Students student) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(student);
        tx1.commit();
        session.close();
    }

    public void delete(Students student) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(student);
        tx1.commit();
        session.close();
    }
    public void deleteAll() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.createQuery("DELETE FROM students").executeUpdate();
        tx1.commit();
        session.close();
    }
    public List<Students> findAll() {
        List<Students> students = (List<Students>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Students").list();
        return students;
    }
}
