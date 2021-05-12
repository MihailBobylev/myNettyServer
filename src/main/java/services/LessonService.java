package services;

import dao.LessonDAO;
import dao.StudentsDAO;
import models.Lesson;
import models.Students;

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
}
