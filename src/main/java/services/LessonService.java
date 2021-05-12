package services;

import dao.LessonDAO;
import dao.StudentsDAO;
import models.Auds;
import models.Lesson;
import models.Students;
import models.Teachers;

import java.util.List;

public class LessonService {
    private LessonDAO lessonDAO = new LessonDAO();

    public LessonService() {
    }

    public Lesson findLesson(int id) {
        return lessonDAO.findLessonById(id);
    }

    public void saveLesson(Lesson lesson) {
        lessonDAO.save(lesson);
    }

    public void deleteLesson(Lesson lesson) {
        lessonDAO.delete(lesson);
    }
    public void deleteAllLesson() {
        lessonDAO.deleteAll();
    }

    public void updateLesson(Lesson lesson) {
        lessonDAO.update(lesson);
    }

    public List<Lesson> findAllLessons() {
        return lessonDAO.findAll();
    }

    public List<Lesson> findByGroup(String group) {
        return lessonDAO.findByGroup(group);
    }

    public List<Lesson> findBySubgroup(String subgroup) {
        return lessonDAO.findBySubgroup(subgroup);
    }

    public List<Lesson> findByTeacherID(Teachers teacher) {
        return lessonDAO.findByTeacherID(teacher);
    }

    public List<Lesson> findByAudID(Auds aud, String numberOfWeek, String numberOfDay, String numberOfClass) {
        return lessonDAO.findByAudID(aud, numberOfWeek, numberOfDay, numberOfClass);
    }
}
