import javassist.expr.NewArray;
import models.*;
import services.*;

import java.io.IOException;
import java.util.List;

public class MainGraph {
    public static void main(String[] args) throws IOException {
        /*Graph graph = new Graph(5);

        graph.insertArea(1,"E", 3, "A");
        graph.insertArea(2,"E", 3, "B");
        graph.insertArea(3,"E", 3, "C");
        graph.insertArea(4,"E", 3, "D");
        graph.insertArea(5,"E", 3, "E");

        graph.insertEdge(0, 1);
        graph.insertEdge(1, 2);
        graph.insertEdge(2, 3);
        graph.insertEdge(3, 4);

        graph.dfs(0);*/

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

        hallwaySevice.saveHallway(hallway);

        Classroom classroom1 = new Classroom(319,"право","первый");
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
        System.out.println(responseData.getTeacherName());
        System.out.println(responseData.getSchedule());*/

        // Расписание группы
        ResponseData responseData = new ResponseData();
        ProcessingHandler p = new ProcessingHandler();
        responseData = p.getSchedule("Институт автоматизированных систем и технологий", "09.03.02 Информационные системы и технологии направленность (профиль) Информационные системы и технологии","17-ИСбо-2");
        System.out.println(responseData.getGroupName());
        System.out.println(responseData.getSchedule());
    }

}
