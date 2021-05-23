import dao.LessonBySubgroupDAO;
import models.*;
import services.*;

import java.io.IOException;
import java.util.List;

public class MainGraph {
    public static void main(String[] args) throws IOException {
        // Граф
        /*AreaService areaService = new AreaService();
        Area area1 = new Area("E",3,"A");
        Area area2 = new Area("E",3,"B");
        areaService.saveArea(area1);
        areaService.saveArea(area2);

        HallwaySevice hallwaySevice = new HallwaySevice();
        Hallway hallway = new Hallway("лево", "право");
        hallway.setStartArea(area1);
        hallway.setEndArea(area2);
        area1.addStartHallway(hallway);
        area2.addEndHallway(hallway);

        hallwaySevice.saveHallway(hallway);*/

        /*Classroom classroom1 = new Classroom(319,"право","первый");
        Classroom classroom2 = new Classroom(321,"право","второй");
        Classroom classroom3 = new Classroom(323,"право","третий");
        classroom1.setHallway(hallway);
        classroom2.setHallway(hallway);
        classroom3.setHallway(hallway);
        hallway.addClassroom(classroom1);
        hallway.addClassroom(classroom2);
        hallway.addClassroom(classroom3);
        hallwaySevice.updateHallway(hallway);*/

        // Аудитории, преподы и группы
       /* AudsService audsService = new AudsService();
        //Auds auds = new Auds("E","325","json");
        //audsService.saveAud(auds);

        StudentsService studentsService = new StudentsService();
        //Students students = new Students("автоматы","инфа","17-ИСбо-2а","json");
        //studentsService.saveStudent(students);

        TeachersService teachersService = new TeachersService();
        //Teachers teachers = new Teachers("Папич","json");
        //teachersService.saveTeacher(teachers);

        List<Auds> la = audsService.findAllAuds();
        System.out.println(la.get(0).getNumber());
        System.out.println(studentsService.findAllStudents().get(0).getGroupp());
        System.out.println(teachersService.findAllTeachers().get(0).getName());*/

        // Расписание препода
        /*ResponseData responseData = new ResponseData();
        ProcessingHandler p = new ProcessingHandler();
        responseData = p.getTeacher("Бабенко А.С.");
        System.out.println("Расписание с сатйта КГУ");
        System.out.println(responseData.getTeacherName());
        System.out.println(responseData.getSchedule());
        System.out.println("-------------------------------");

        responseData = p.getSchedule("Институт автоматизированных систем и технологий", "09.03.02 Информационные системы и технологии направленность (профиль) Информационные системы и технологии","17-ИСбо-2");
        System.out.println(responseData.getGroupName());
        System.out.println(responseData.getSchedule());
        System.out.println("-------------------------------");

        responseData = p.getCurrentClass("Е","325","1","2","0");
        System.out.println(responseData.getClassName());
        System.out.println(responseData.getGroupName());
        System.out.println(responseData.getTeacherName());
        System.out.println("-------------------------------");*/


        // Расписание группы
        //ResponseData responseData = new ResponseData();
        /*List<Students> groupSchedule;
        StudentsService studentsService = new StudentsService();
        groupSchedule =  studentsService.findByGroup("17-ИСбо-2");
        System.out.println("Расписание из БД");
        System.out.println(groupSchedule.get(0).getGroupp());
        System.out.println(groupSchedule.get(0).getSubgroup());
        System.out.println("-------------------------------");

        List<Auds> audsSchedule;
        AudsService audsService = new AudsService();
        audsSchedule = audsService.findByAud("Е","325");
        System.out.println(audsSchedule.get(0).getCorp());
        System.out.println(audsSchedule.get(0).getNumber());
        System.out.println("-------------------------------");

        List<Teachers> teacherSchedule;
        TeachersService teachersService = new TeachersService();
        teacherSchedule = teachersService.findTeacherByName("Бабенко А.С.");
        System.out.println(teacherSchedule.get(0).getName());*/

        //ProcessingHandler p = new ProcessingHandler();
        //responseData = p.getSchedule("Институт автоматизированных систем и технологий", "09.03.02 Информационные системы и технологии направленность (профиль) Информационные системы и технологии","17-ИСбо-2");
        //System.out.println(responseData.getGroupName());
        //System.out.println(responseData.getSchedule());
//------------------Тест работы с БД
//        AudsService audsService = new AudsService();
//        Auds auds = new Auds("E","325");
//        audsService.saveAud(auds);
//
        /*TeachersService teachersService = new TeachersService();
        List<Teachers> teachers = teachersService.findTeacherByName("ВУЦ");
        System.out.println(teachers.size());*/
//        Teachers teachers = new Teachers("Папич");
//        teachersService.saveTeacher(teachers);
//
//        StudentsService studentsService = new StudentsService();
//        Students students = new Students("inst","direct","17-is","a");
//        studentsService.saveStudent(students);
//
        /*LessonService lessonService = new LessonService();
        TeachersService teachersService = new TeachersService();
        List<Teachers> teachers = teachersService.findTeacherByName("Денисов А.Р.");
        List<Lesson> lessons = lessonService.findByTeacherID(teachers.get(0));
        System.out.println("Пары препода");
        for (Lesson l: lessons) {
            System.out.println(l.getTeacher().getName());
            System.out.println(l.getAud().getNumber());
            System.out.println(l.getGroupp());
            System.out.println(l.getSubgroup());
            System.out.println(l.getNumberOfClass());
            System.out.println(l.getNumberOfDay());
            System.out.println(l.getNumberOfWeek());
            System.out.println(l.getLessonByGroup());
            System.out.println(l.getLessontype());
            System.out.println("-------------------------");
        }*/
/*        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
          lessons.clear();
          lessons = lessonService.findByGroup("18-ВТбо-1");
        System.out.println("Пары группы");
        for (Lesson l: lessons) {
            System.out.println(l.getAud().getNumber());
            System.out.println(l.getGroupp());
            System.out.println(l.getSubgroup());
            System.out.println(l.getNumberOfClass());
            System.out.println(l.getNumberOfDay());
            System.out.println(l.getNumberOfWeek());
            System.out.println(l.getLessonByGroup());
            System.out.println(l.getLessontype());
            System.out.println("-------------------------");
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        lessons.clear();
          lessons = lessonService.findBySubgroup("18-ВТбо-1б");
        System.out.println("Пары подгруппы");
        for (Lesson l: lessons) {
            System.out.println(l.getAud());
            System.out.println(l.getGroupp());
            System.out.println(l.getSubgroup());
            System.out.println(l.getNumberOfClass());
            System.out.println(l.getNumberOfDay());
            System.out.println(l.getNumberOfWeek());
            System.out.println(l.getLessonByGroup());
            System.out.println(l.getLessontype());
            System.out.println("-------------------------");
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        AudsService audsService = new AudsService();
        lessons.clear();
          lessons = lessonService.findByAudID(audsService.findAuds(832), "0","4","4");
        System.out.println("Пара в аудитории");
        for (Lesson l: lessons) {
            System.out.println(l.getAud().getNumber());
            System.out.println(l.getGroupp());
            System.out.println(l.getSubgroup());
            System.out.println(l.getNumberOfClass());
            System.out.println(l.getNumberOfDay());
            System.out.println(l.getNumberOfWeek());
            System.out.println(l.getLessonByGroup());
            System.out.println(l.getLessontype());
            System.out.println("-------------------------");
        }*/
//        Lesson lesson = new Lesson("лекция", "0","Мультимедиа", "17-is","a","4","1");
//        lesson.setTeacher(teachers);
//        lesson.setAud(auds);
//        //auds.addLesson(lesson);
//        lessonService.saveLesson(lesson);
//
//        LessonBySubgroupDAO lessonBySubgroupDAO = new LessonBySubgroupDAO();
//        LessonBySubgroup lessonBySubgroup = new LessonBySubgroup();
//        lessonBySubgroup.setLesson(lesson);
//        lessonBySubgroup.setStudent(students);
//        lessonBySubgroupDAO.save(lessonBySubgroup);
        ProcessingHandler p = new ProcessingHandler();
        //p.FillAuds();
        //p.FillTeachers();
        //p.FillLessons();
        //p.FillLessonBySubgroup();
        /*p.FillFirstFloor();
        p.FillSecondFloor();
        p.FillThirdFloor();
        p.FillFourthFloor();
        p.FillFifthFloor();*/
    }

}
