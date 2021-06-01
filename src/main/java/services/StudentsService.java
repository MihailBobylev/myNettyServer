package services;

import dao.AudsDAO;
import dao.StudentsDAO;
import models.Auds;
import models.Students;

import java.util.List;

public class StudentsService {
    private StudentsDAO studentsDAO = new StudentsDAO();

    public StudentsService() {
    }

    public Students findStudent(int id) {
        return studentsDAO.findStudentById(id);
    }

    public void saveStudent(Students student) {
        studentsDAO.save(student);
    }

    public void deleteStudent(Students student) {
        studentsDAO.delete(student);
    }
    public void deleteAllStudents() {
        studentsDAO.deleteAll();
    }
    public void updateStudent(Students student) {
        studentsDAO.update(student);
    }

    public List<Students> findAllStudents() {
        return studentsDAO.findAll();
    }

    public List<Students> findByGroup(String group) {
        return studentsDAO.findByGroup(group);
    }
    public List<Students> findBySubgroup(String subGroup) {
        return studentsDAO.findBySubgroup(subGroup);
    }
}
