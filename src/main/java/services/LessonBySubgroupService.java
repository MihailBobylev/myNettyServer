package services;

import dao.LessonBySubgroupDAO;
import dao.LessonDAO;
import models.Lesson;
import models.LessonBySubgroup;

import java.util.List;

public class LessonBySubgroupService {
    private LessonBySubgroupDAO lessonBySubgroupDAO = new LessonBySubgroupDAO();

    public LessonBySubgroupService() {
    }

    public LessonBySubgroup findLesson(int id) {
        return lessonBySubgroupDAO.findLessonById(id);
    }

    public List<LessonBySubgroup> findByLessonId(int id) {
        return lessonBySubgroupDAO.findByLessonId(id);
    }

    public List<LessonBySubgroup> findByStudentById(int id) {
        return lessonBySubgroupDAO.findByStudentId(id);
    }

    public void saveLesson(LessonBySubgroup lesson) {
        lessonBySubgroupDAO.save(lesson);
    }

    public void deleteLesson(LessonBySubgroup lesson) {
        lessonBySubgroupDAO.delete(lesson);
    }
    public void deleteAllLessonsBySubgroup() {
        lessonBySubgroupDAO.deleteAll();
    }
    public void updateLesson(LessonBySubgroup lesson) {
        lessonBySubgroupDAO.update(lesson);
    }

    public List<LessonBySubgroup> findAllLessons() {
        return lessonBySubgroupDAO.findAll();
    }
}
