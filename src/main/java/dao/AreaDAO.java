package dao;

import config.HibernateSessionFactoryUtil;
import models.Area;
import models.Hallway;
import models.Teachers;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class AreaDAO {
    public Area findAreaById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Area.class, id);
    }
    public List<Area> findByAreasName(String name) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Area> areas =
                session.createCriteria(Area.class)
                        .add(Restrictions.eq("name", name))
                        .list();
        return areas;
    }
    public void save(Area area) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(area);
        tx1.commit();
        session.close();
    }

    public void update(Area area) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(area);
        tx1.commit();
        session.close();
    }

    public void delete(Area area) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(area);
        tx1.commit();
        session.close();
    }

    public Hallway findHallwayById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Hallway.class, id);
    }

    public List<Area> findAll() {
        List<Area> areas = (List<Area>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Area").list();
        return areas;
    }
}
