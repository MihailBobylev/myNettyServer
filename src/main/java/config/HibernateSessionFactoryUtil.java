package config;

import models.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactoryUtil {
    private static SessionFactory sessionFactory;

    private HibernateSessionFactoryUtil() {}

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(Area.class);
                configuration.addAnnotatedClass(Classroom.class);
                configuration.addAnnotatedClass(Hallway.class);
                configuration.addAnnotatedClass(Auds.class);
                configuration.addAnnotatedClass(Students.class);
                configuration.addAnnotatedClass(Teachers.class);
                configuration.addAnnotatedClass(Lesson.class);
                configuration.addAnnotatedClass(LessonBySubgroup.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());

            } catch (Exception e) {
                System.out.println("Исключение!" + e);
            }
        }
        return sessionFactory;
    }
}
