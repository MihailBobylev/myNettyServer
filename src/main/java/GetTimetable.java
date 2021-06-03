import models.*;
import services.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetTimetable {
    AudsService audsService = new AudsService();
    StudentsService studentsService = new StudentsService();
    TeachersService teachersService = new TeachersService();
    LessonService lessonService = new LessonService();
    LessonBySubgroupService lessonBySubgroupService = new LessonBySubgroupService();

    List<Students> groupSchedule = new ArrayList<Students>();
    List<Auds> audsSchedule = new ArrayList<Auds>();
    List<Teachers> teacherSchedule = new ArrayList<Teachers>();
    List<Lesson> allLessons = new ArrayList<Lesson>();

    String teacherName = "";
    String lessonName = "";
    String lessonType = "";
    String groupName = "";
    String lessonNumber = "";
    String corp = "";
    String auditor = "";
    String institute = "";
    String direction = "";
    String groupp = "";
    String numberOfDay = "";
    String numberOfClass = "";
    String numberOfWeek = "";
    String subgroup = "";

    public ResponseData getAudSchedule(RequestData requestData){
        ResponseData responseData = new ResponseData();
        audsSchedule = audsService.findByAud(requestData.getCorp(), requestData.getAuditor());
        if (audsSchedule.size() == 0)
            return responseData;

        allLessons =  lessonService.findByAudID(audsSchedule.get(0), requestData.getWeek(),requestData.getDayOfWeek(),requestData.getLessonNumber());

        if (allLessons.size() == 0){
            allLessons =  lessonService.findByAudID(audsSchedule.get(0), "2",requestData.getDayOfWeek(),requestData.getLessonNumber());
            if(allLessons.size() == 0)
                return responseData;
        }
        for (Lesson l: allLessons) {
            teacherName += l.getTeacher().getName() + "@";
            lessonName += l.getLessonByGroup() + "@";
            lessonType += l.getLessontype() + "@";
            groupName += l.getGroupp() + "@";
            auditor += l.getAud().getNumber() + "@";
            numberOfDay += l.getNumberOfDay() + "@";
            numberOfWeek += l.getNumberOfWeek() + "@";
            numberOfClass += l.getNumberOfClass() + "@";
            subgroup += l.getSubgroup() + "@";
        }

        responseData.setTeacherName(teacherName);
        responseData.setLessonName(lessonName);
        responseData.setLessonType(lessonType);
        responseData.setGroupName(groupName);
        responseData.setAuditor(auditor);
        responseData.setNumberOfDay(numberOfDay);
        responseData.setNumberOfClass(numberOfClass);
        responseData.setNumberOfWeek(numberOfWeek);
        responseData.setSubgroup(subgroup);
        return responseData;
    }
    public ResponseData getTeacherSchedule(RequestData requestData){
        ResponseData responseData = new ResponseData();
        teacherSchedule =  teachersService.findTeacherByName(requestData.getTeacherName());
        if(teacherSchedule.size() == 0)
            return responseData;

        allLessons =  lessonService.findByTeacherID(teacherSchedule.get(0));

        Auds auds2;
        if (allLessons.size() == 0)
            return  responseData;

        for (Lesson l: allLessons) {
            auds2 = audsService.findAuds(l.getAud().getId());
            lessonNumber += l.getNumberOfClass() + "@";
            corp += auds2.getCorp() + "@";
            auditor += auds2.getNumber() + "@";
            lessonName += l.getLessonByGroup() + "@";
            lessonType += l.getLessontype() + "@";
            groupName += l.getGroupp() + "@";
            teacherName += l.getTeacher().getName() + "@";
            subgroup += l.getSubgroup() + "@";
            numberOfDay += l.getNumberOfDay() + "@";
            numberOfWeek += l.getNumberOfWeek() + "@";
            numberOfClass += l.getNumberOfClass() + "@";
        }
        responseData.setLessonNumber(lessonNumber);
        responseData.setCorps(corp);
        responseData.setAuditor(auditor);
        responseData.setLessonName(lessonName);
        responseData.setLessonType(lessonType);
        responseData.setGroupName(groupName);
        responseData.setTeacherName(teacherName);
        responseData.setNumberOfDay(numberOfDay);
        responseData.setNumberOfClass(numberOfClass);
        responseData.setNumberOfWeek(numberOfWeek);
        responseData.setSubgroup(subgroup);
        return responseData;
    }
    public ResponseData getStudentsSchedule(RequestData requestData){
        ResponseData responseData = new ResponseData();
        allLessons = lessonService.findByGroup(requestData.getGroup());

        if (allLessons.size() == 0)
            return responseData;

        for (Lesson l: allLessons) {
            Auds auds = audsService.findAuds(l.getId());
            lessonNumber += l.getNumberOfClass() + "@";
            corp += auds.getCorp() + "@";
            auditor += auds.getNumber() + "@";
            lessonName += l.getLessonByGroup() + "@";
            lessonType += l.getLessontype() + "@";
            teacherName += l.getTeacher().getName() + "@";
            groupName += l.getGroupp() + "@";
            subgroup += l.getSubgroup() + "@";
            numberOfDay += l.getNumberOfDay() + "@";
            numberOfWeek += l.getNumberOfWeek() + "@";
            numberOfClass += l.getNumberOfClass() + "@";
        }
        responseData.setLessonNumber(lessonNumber);
        responseData.setCorps(corp);
        responseData.setAuditor(auditor);
        responseData.setLessonName(lessonName);
        responseData.setLessonType(lessonType);
        responseData.setTeacherName(teacherName);
        responseData.setGroupName(groupName);
        responseData.setNumberOfDay(numberOfDay);
        responseData.setNumberOfClass(numberOfClass);
        responseData.setNumberOfWeek(numberOfWeek);
        responseData.setSubgroup(subgroup);
        return responseData;
    }
    public ResponseData fillClientDB(){
        ResponseData responseData = new ResponseData();
        groupSchedule = studentsService.findAllStudents();// институт, направление, группа

        if (groupSchedule.size() == 0)
            return responseData;

        for (Students s: groupSchedule) {
            institute += s.getInstitute() + "@";
            direction += s.getDirection() + "@";
            groupp += s.getGroupp() + "@";
        }
        responseData.setInstitute(institute);
        responseData.setDirection(direction);
        responseData.setGroupName(groupp);

        teacherSchedule = teachersService.findAllTeachers();// имя преподавателя
        if (teacherSchedule.size() == 0)
            return responseData;

        for (Teachers t: teacherSchedule) {
            teacherName += t.getName() + "@";
        }
        responseData.setTeacherName(teacherName);

        audsSchedule = audsService.findAllAuds();// корпуса и аудитории

        if (audsSchedule.size() == 0)
            return responseData;

        for (Auds a: audsSchedule) {
            corp += a.getCorp() + "@";
            auditor += a.getNumber() + "@";
        }
        responseData.setCorps(corp);
        responseData.setAuditor(auditor);
        return responseData;
    }
    public List<String> buildRoute(String startRoomNumber, String endRoomNumber) throws Exception {
        List<String> route = new ArrayList<>();
        HallwaySevice hallwaySevice = new HallwaySevice();
        Classroom startRoom = hallwaySevice.getClassroom(startRoomNumber);
        Hallway startHallway = startRoom.getHallway();
        Classroom endRoom = hallwaySevice.getClassroom(endRoomNumber);
        Hallway endHallway = endRoom.getHallway();
        List<Hallway> hallways = pathFind(startHallway.getStartArea().getId(), endHallway.getEndArea().getId());
        for (int i = 0; i < hallways.size()-1; i++) {

            if(hallways.get(i).getEndArea().getId().equals(hallways.get(i+1).getStartArea().getId())){
                route.add(hallways.get(i).getOrientationStart());
            }
            else
                route.add(hallways.get(i).getOrientationEnd());

            if (hallways.size()-2 == i && hallways.get(i).getEndArea().getId().equals(hallways.get(i+1).getStartArea().getId()))
                route.add(hallways.get(i+1).getOrientationStart());
            else if(hallways.size()-2 == i && !hallways.get(i).getEndArea().getId().equals(hallways.get(i+1).getStartArea().getId()))
                route.add(hallways.get(i+1).getOrientationEnd());

        }
        Classroom classroom = hallwaySevice.getClassroom(endRoomNumber);
        route.add(classroom.getSide());
        route.add(classroom.getPosition());
        return route;
    }
    public List<Hallway> pathFind(Integer startId, Integer endId) throws Exception {
        Map<Integer, Integer> dickt = new HashMap<>();
        HallwaySevice hallwaySevice = new HallwaySevice();
        AreaService areaService = new AreaService();
        Area startArea = areaService.findArea(startId);
        List<Area> areas = areaService.findAllAreas();
        for ( Area area: areas) {
            if (area.getHousing().equals(startArea.getHousing())){
                dickt.put(area.getId(), -1);
            }
        }
        dickt.replace(startId, 0);
        if (!dickt.containsKey(endId))
            throw new Exception("Конечой площадки не существует в данном корпусе");
        Integer step = 0;
        while (dickt.get(endId) == -1){
            for (Map.Entry<Integer, Integer> kV: dickt.entrySet()) {
                if(kV.getValue().equals(step)){
                    List<Hallway> hallways = hallwaySevice.getConnectedHallways(kV.getKey());
                    for (Hallway h: hallways) {
                        Integer otherEnd = hallwaySevice.getOtherEnd(h,kV.getKey()).getId();
                        dickt.replace(otherEnd, -1,step+1);
                    }
                }
            }
            step++;
        }
        Integer currentAreaId = endId;
        List<Hallway> route = new ArrayList<>();
        while (!currentAreaId.equals(startId)){
            List<Hallway> hallways = hallwaySevice.getConnectedHallways(currentAreaId);
            Boolean found = false;
            for (Hallway h: hallways) {
                Integer otherEnd = hallwaySevice.getOtherEnd(h,currentAreaId).getId();
                if (dickt.get(otherEnd) == dickt.get(currentAreaId)-1 ){
                    route.add(0, h);
                    currentAreaId = otherEnd;
                    found = true;
                    break;
                }
            }
            if (!found)
                throw new Exception();// если маршрут разорван
        }
        return route;
    }
}
