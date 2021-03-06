package services;

import dao.TeachersDAO;
import models.Teachers;

import java.util.List;

public class TeachersService {
    private TeachersDAO teachersDAO = new TeachersDAO();

    public TeachersService() {
    }

    public Teachers findTeacher(int id) {
        return teachersDAO.findTeacherById(id);
    }

    public void saveTeacher(Teachers teacher) {
        teachersDAO.save(teacher);
    }

    public void deleteTeacher(Teachers teacher) {
        teachersDAO.delete(teacher);
    }
    public void deleteAll() {
        teachersDAO.deleteAll();
    }

    public void updateTeacher(Teachers teacher) {
        teachersDAO.update(teacher);
    }

    public List<Teachers> findAllTeachers() {
        return teachersDAO.findAll();
    }

    public List<Teachers> findTeacherByName(String name) {
        return teachersDAO.findByTeacher(name);
    }
}
