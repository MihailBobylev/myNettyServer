import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import models.*;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONArray;
import org.json.JSONObject;
import services.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class ProcessingHandler extends ChannelInboundHandlerAdapter {
    private Calendar calendar = Calendar.getInstance();
    AudsService audsService = new AudsService();
    StudentsService studentsService = new StudentsService();
    TeachersService teachersService = new TeachersService();
    LessonService lessonService = new LessonService();
    LessonBySubgroupService lessonBySubgroupService = new LessonBySubgroupService();

    AreaService areaService = new AreaService();
    HallwaySevice hallwaySevice = new HallwaySevice();

    List<Students> groupSchedule = new ArrayList<Students>();
    List<Auds> audsSchedule = new ArrayList<Auds>();
    List<Teachers> teacherSchedule = new ArrayList<Teachers>();
    List<Lesson> allLessons = new ArrayList<Lesson>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestData requestData = (RequestData) msg;
        String schedule = "";
        //ResultSet rs;
        //ResponseData responseData = new ResponseData();
        // Поиск данных в БД
        // case по параметру (0, 1, 2, 3), опредлеляющему запрос пользователя
        ResponseData responseData = new ResponseData();
        int flag = 1;
        switch (flag){
            case 0: // расписание аудитории
                audsSchedule =  audsService.findByAud(requestData.getCorp(), requestData.getAuditor());
                if (audsSchedule.size() != 0){
                    responseData.setNumber(audsSchedule.get(0).getNumber());
                }else
                    responseData = getCurrentClass(requestData.getGroup(), requestData.getAuditor(), requestData.getDayOfWeek(), requestData.getLessonNumber(), requestData.getWeek());
                audsSchedule.clear();
                break;
            case 1: // расписание препода
                teacherSchedule =  teachersService.findTeacherByName(requestData.getName());
                /*if (teacherSchedule.size() != 0){
                    responseData.setTeacherName(teacherSchedule.get(0).getName());
                }else
                    responseData = getTeacher(requestData.getName());*/
                teacherSchedule.clear();
                break;
            case 2: // расписание группы
                groupSchedule =  studentsService.findByGroup(requestData.getGroup());
                if (groupSchedule.size() != 0){
                    responseData.setGroupName(groupSchedule.get(0).getGroupp());
                    responseData.setSchedule(groupSchedule.get(0).getSubgroup());
                }else
                    responseData = getSchedule(requestData.getInstitute(), requestData.getDirection(), requestData.getGroup());
                groupSchedule.clear();
                break;
            case 3: // поиск пути
                break;
        }

        //responseData.setIntValue(requestData.getIntValue() * 2);
        ChannelFuture future = ctx.writeAndFlush(responseData); //responseData
        future.addListener(ChannelFutureListener.CLOSE);
        System.out.println(requestData.getName());
    }
    public String getUrl(){

        return "https://ksu.edu.ru/timetable/" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH)/8 + 1) + "/timetable.php";

    }
    public ResponseData getCurrentClass(String build, String room, String dayOfWeek, String lessonNumber, String week) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "getbuildings");

        String buildingsJSON = doPostQuery(getUrl(), params); // список всех корпусов
        JSONArray jObj = new JSONArray(buildingsJSON);
        String post_id = null;
        for (int i = 0; i < jObj.length(); i++){
            String s = jObj.getJSONObject(i).getString("title");
            if (jObj.getJSONObject(i).getString("title").equals(build)) {
                post_id = jObj.getJSONObject(i).getString("id");
                break;
            }
        }
        if (post_id == null)
            return null;

        params = new HashMap<String, String>();
        params.put("action","getauds");
        params.put("id", post_id);// корпус Е

        String roomsJSON = doPostQuery(getUrl(), params); //"https://ksu.edu.ru/timetable/2020_1/timetable.php"
        jObj = new JSONArray(roomsJSON);
        post_id = null;
        for (int i = 0; i < jObj.length(); i++){
            if (jObj.getJSONObject(i).getString("title").equals("Е-" + room)) {
                post_id = jObj.getJSONObject(i).getString("id");
                break;
            }
        }
        if (post_id == null)
            return null;

        params.clear();
        params.put("action", "gettimetable");
        params.put("mode","aud");
        params.put("id", post_id);

        String ttJSON = doPostQuery(getUrl(), params); //"https://ksu.edu.ru/timetable/2020_1/timetable.php"
        jObj = new JSONArray(ttJSON);

        // Записываем расписание для аудитории в БД
        audsService.saveAud(new Auds(build, room));
        //---------------------------------------------

        String x = dayOfWeek;
        String y = lessonNumber;
        String n = week;
        String className = null;
        String groupName = null;
        String teacherName = null;
        for (int i = 0; i < jObj.length(); i++){
            JSONObject item = jObj.getJSONObject(i);
            if (item.getString("x").equals(x) && item.getString("y").equals(y) && item.getString("n").equals(n)){
                className = item.getString("subject1");
                groupName = item.isNull("subgroup")
                        ? item.getString("subject2")
                        : item.getString("subgroup");
                teacherName = item.getString("subject3");
                break;
            }

        }
        if (className == null)
            return null;
        else
        {
            ResponseData l = new ResponseData();
            l.setClassName(className);
            l.setGroupName(groupName);
            l.setTeacherName(teacherName);
            return l;
        }

    }

    public void FillTeachers() throws IOException {
        teachersService.deleteAll();
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "getteachers");

        String teachersJSON = doPostQuery(getUrl(), params); // список всех преподов
        JSONArray jObj = new JSONArray(teachersJSON);
        teachersService.saveTeacher(new Teachers("Неизвестно"));
        for (int i = 0; i < jObj.length(); i++){
            teachersService.saveTeacher(new Teachers(jObj.getJSONObject(i).getString("title")));
        }

        //--------------------------------

        /*params = new HashMap<String, String>();
        params.put("action", "gettimetable");
        params.put("mode","teacher");
        params.put("id", post_id);

        teachersJSON = doPostQuery(getUrl(), params); // получаем всё расписание конкретного препода
        jObj = new JSONArray(teachersJSON);
        if (jObj.length() == 0)
            return null;
        else
        {
            // Записываем расписание для препода в БД
            teachersService.saveTeacher(new Teachers(name));
            //---------------------------------------------

            ResponseData l = new ResponseData();
            l.setTeacherName(name);
            l.setSchedule(jObj.toString());
            return l;
        }*/
    }
    public void FillAuds() throws IOException{
        audsService.deleteAllAuds();
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "getbuildings");

        String buildingsJSON = doPostQuery(getUrl(), params);
        JSONArray jObj = new JSONArray(buildingsJSON); // список всех корпусов
        JSONArray jAuds;
        audsService.saveAud(new Auds("Неизвестно", "Неизвестно"));
        for (int i = 0; i < jObj.length(); i++){
            params.clear();
            params.put("action","getauds");
            params.put("id", jObj.getJSONObject(i).getString("id"));
            buildingsJSON = doPostQuery(getUrl(), params);// список всех аудиторий
            jAuds = new JSONArray(buildingsJSON);
            for (int j = 0; j < jAuds.length(); j++){
                audsService.saveAud(new Auds(jObj.getJSONObject(i).getString("title"), jAuds.getJSONObject(j).getString("title")));
            }

            System.out.println("All auds in: " + jObj.getJSONObject(i).getString("title"));
        }
    }
    public void FillStudents() throws IOException{
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "getfaculties");

        String facultiesJSON = doPostQuery(getUrl(), params);
        JSONArray jObj = new JSONArray(facultiesJSON); // список всех факультетов
        JSONArray jDirections;
        JSONArray jGroups;
        for (int i = 0; i < jObj.length(); i++){ // по факультетам
            params.clear();
            params.put("action", "getbranches");
            params.put("id", jObj.getJSONObject(i).getString("id"));
            facultiesJSON = doPostQuery(getUrl(), params);
            jDirections = new JSONArray(facultiesJSON); // список всех направлений
            for (int j = 0; j < jDirections.length(); j++){// по направлениям
                params.clear();
                params.put("action", "getgroups");
                params.put("id", jDirections.getJSONObject(j).getString("id"));
                facultiesJSON = doPostQuery(getUrl(), params);
                jGroups = new JSONArray(facultiesJSON); // список всех групп
                for (int x = 0; x < jGroups.length(); x++){// по группам

                    studentsService.saveStudent(new Students(
                                    jObj.getJSONObject(i).getString("title")
                                    , jDirections.getJSONObject(j).getString("title")
                                    , jGroups.getJSONObject(x).getString("title")
                                    , jGroups.getJSONObject(x).getString("title")));
                }
                System.out.println("Save: " + jDirections.getJSONObject(j).getString("title"));
            }
        }
    }
    public void FillLessons() throws IOException{
        lessonService.deleteAllLesson();
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "getfaculties");

        String facultiesJSON = doPostQuery(getUrl(), params);
        JSONArray jObj = new JSONArray(facultiesJSON); // список всех факультетов
        JSONArray jDirections;
        JSONArray jGroups;
        JSONArray jShedule;
        for (int i = 0; i < jObj.length(); i++){ // по факультетам
            params.clear();
            params.put("action", "getbranches");
            params.put("id", jObj.getJSONObject(i).getString("id"));
            facultiesJSON = doPostQuery(getUrl(), params);
            jDirections = new JSONArray(facultiesJSON); // список всех направлений
            for (int j = 0; j < jDirections.length(); j++){// по направлениям
                params.clear();
                params.put("action", "getgroups");
                params.put("id", jDirections.getJSONObject(j).getString("id"));
                facultiesJSON = doPostQuery(getUrl(), params);
                jGroups = new JSONArray(facultiesJSON); // список всех групп
                for (int x = 0; x < jGroups.length(); x++){// по группам

                    /*List<Lesson> lesson2 = lessonService.findByGroup(jGroups.getJSONObject(x).getString("title"));
                    if (lesson2.size() != 0)
                        continue;*/

                    params.clear();
                    params.put("action", "gettimetable");
                    params.put("mode", "student");
                    params.put("id", jGroups.getJSONObject(x).getString("id"));
                    facultiesJSON = doPostQuery(getUrl(), params);
                    jShedule = new JSONArray(facultiesJSON); // Расписание для группы
                    String[] tmp = {"Неизвестно", "Неизвестно"};
                    String[] corpAud = new String[2];
                    for (int z = 0; z < jShedule.length(); z++){ // заполнение расписания подгруппы
                        try {
                            if (jShedule.getJSONObject(z).isNull("subject2"))
                            {
                                corpAud = tmp;
                            }
                            else
                                corpAud = jShedule.getJSONObject(z).getString("subject2").split("-");
                        }catch (ArrayIndexOutOfBoundsException e){
                            System.out.println(e.getMessage());
                        }


                        audsSchedule.clear();
                        if (jShedule.getJSONObject(z).isNull("subject2"))
                            audsSchedule = audsService.findByAud("Неизвестно","Неизвестно");
                        else
                            audsSchedule = audsService.findByAud(corpAud[0], jShedule.getJSONObject(z).getString("subject2")); // поиск аудитории

                        if (audsSchedule.size() == 0)
                            audsSchedule = audsService.findByAud("Неизвестно","Неизвестно");

                        teacherSchedule.clear();
                        if (jShedule.getJSONObject(z).isNull("subject3"))
                            teacherSchedule = teachersService.findTeacherByName("Неизвестно");
                        else
                            teacherSchedule = teachersService.findTeacherByName(jShedule.getJSONObject(z).getString("subject3"));// поиск препода

                        if (teacherSchedule.size() == 0)
                            teacherSchedule = teachersService.findTeacherByName("Неизвестно");

                        Lesson lesson;
                        if (jShedule.getJSONObject(z).isNull("subgroup"))
                        {
                            lesson = new Lesson(
                                    jShedule.getJSONObject(z).getString("lessontype")
                                    , jShedule.getJSONObject(z).getString("n")
                                    , jShedule.getJSONObject(z).getString("subject1")
                                    , jGroups.getJSONObject(x).getString("title")
                                    , jGroups.getJSONObject(x).getString("title")
                                    , jShedule.getJSONObject(z).getString("y")
                                    , jShedule.getJSONObject(z).getString("x"));
                        }else {
                            groupSchedule = studentsService.findBySubgroup(jShedule.getJSONObject(z).getString("subgroup"));
                            lesson = new Lesson(
                                    jShedule.getJSONObject(z).getString("lessontype")
                                    , jShedule.getJSONObject(z).getString("n")
                                    , jShedule.getJSONObject(z).getString("subject1")
                                    , jGroups.getJSONObject(x).getString("title")
                                    , jShedule.getJSONObject(z).getString("subgroup")
                                    , jShedule.getJSONObject(z).getString("y")
                                    , jShedule.getJSONObject(z).getString("x"));
                        }

                        lesson.setAud(audsSchedule.get(0));
                        lesson.setTeacher(teacherSchedule.get(0));
                        lessonService.saveLesson(lesson);
                    }
                    System.out.println("Сохранено для подгруппы: " + jGroups.getJSONObject(x).getString("title"));
                }
                System.out.println("Save for: " + jDirections.getJSONObject(j).getString("title"));
            }
        }
    }
    public void FillLessonBySubgroup(){
        allLessons = lessonService.findAllLessons();
        LessonBySubgroup lessonBySubgroup;
        for (Lesson l: allLessons) {// заполнение смежной таблицы (подгруппа - занятие)
            groupSchedule = studentsService.findByGroup(l.getGroupp());
            lessonBySubgroup = new LessonBySubgroup();
            lessonBySubgroup.setStudent(groupSchedule.get(0));
            lessonBySubgroup.setLesson(l);
            lessonBySubgroupService.saveLesson(lessonBySubgroup);
        }
        System.out.println("Save all: LessonBySubgroup");
    }
    public void FillFirstFloor(){
        // Первый этаж
        Area area1 = new Area("E",1,"A1");
        Area area2 = new Area("E",1,"Б1");
        Area area3 = new Area("E",1,"В1");
        Area area4 = new Area("E",1,"Л1");
        Area area5 = new Area("E",1,"Д1");
        Area area6 = new Area("E",1,"Е1");
        areaService.saveArea(area1);
        areaService.saveArea(area2);
        areaService.saveArea(area3);
        areaService.saveArea(area4);
        areaService.saveArea(area5);
        areaService.saveArea(area6);

        Hallway hallway1 = new Hallway("лево","право");
        hallway1.setStartArea(area2);
        hallway1.setEndArea(area1);
        Hallway hallway2 = new Hallway("лево","право");
        hallway2.setStartArea(area2);
        hallway2.setEndArea(area3);
        Hallway hallway3 = new Hallway("лево","право");
        hallway3.setStartArea(area4);
        hallway3.setEndArea(area2);
        Hallway hallway4 = new Hallway("лево","право");
        hallway4.setStartArea(area4);
        hallway4.setEndArea(area5);
        Hallway hallway5 = new Hallway("лево","право");
        hallway5.setStartArea(area5);
        hallway5.setEndArea(area6);
        hallwaySevice.saveHallway(hallway1);
        hallwaySevice.saveHallway(hallway2);
        hallwaySevice.saveHallway(hallway3);
        hallwaySevice.saveHallway(hallway4);
        hallwaySevice.saveHallway(hallway5);

        Classroom classroom1 = new Classroom("101","лево","второй");
        Classroom classroom2 = new Classroom("102","право","четвертый");
        Classroom classroom3 = new Classroom("103","право","третий");
        Classroom classroom4 = new Classroom("104","лево","первый");
        Classroom classroom5 = new Classroom("105","право","второй");
        Classroom classroom6 = new Classroom("105а","право","первый");
        classroom1.setHallway(hallway2);
        classroom2.setHallway(hallway2);
        classroom3.setHallway(hallway2);
        classroom4.setHallway(hallway2);
        classroom5.setHallway(hallway2);
        classroom6.setHallway(hallway2);
        hallway2.addClassroom(classroom1);
        hallway2.addClassroom(classroom2);
        hallway2.addClassroom(classroom3);
        hallway2.addClassroom(classroom4);
        hallway2.addClassroom(classroom5);
        hallway2.addClassroom(classroom6);

        Classroom classroom7 = new Classroom("106","лево","первый");
        Classroom classroom8 = new Classroom("107","лево","второй");
        Classroom classroom9 = new Classroom("108","лево","третий");
        Classroom classroom10 = new Classroom("109","лево","четвертый");
        classroom7.setHallway(hallway1);
        classroom8.setHallway(hallway1);
        classroom9.setHallway(hallway1);
        classroom10.setHallway(hallway1);
        hallway1.addClassroom(classroom7);
        hallway1.addClassroom(classroom8);
        hallway1.addClassroom(classroom9);
        hallway1.addClassroom(classroom10);

        Classroom classroom11 = new Classroom("100","право","первый");
        Classroom classroom12 = new Classroom("99","право","второй");
        Classroom classroom13 = new Classroom("115","право","третий");
        Classroom classroom14 = new Classroom("113","право","четвертый");
        Classroom classroom15 = new Classroom("113а","право","четвертый");
        Classroom classroom16 = new Classroom("111","право","пятый");
        Classroom classroom17 = new Classroom("110","право","шестой");
        Classroom classroom18 = new Classroom("97","право","седьмой");
        Classroom classroom19 = new Classroom("118","лево","первый");
        Classroom classroom20 = new Classroom("117","лево","второй");
        Classroom classroom21 = new Classroom("116","лево","третий");
        Classroom classroom22 = new Classroom("114","лево","четвертый");
        Classroom classroom23 = new Classroom("112","лево","пятый");
        classroom11.setHallway(hallway3);
        classroom12.setHallway(hallway3);
        classroom13.setHallway(hallway3);
        classroom14.setHallway(hallway3);
        classroom15.setHallway(hallway3);
        classroom16.setHallway(hallway3);
        classroom17.setHallway(hallway3);
        classroom18.setHallway(hallway3);
        classroom19.setHallway(hallway3);
        classroom20.setHallway(hallway3);
        classroom21.setHallway(hallway3);
        classroom22.setHallway(hallway3);
        classroom23.setHallway(hallway3);
        hallway3.addClassroom(classroom11);
        hallway3.addClassroom(classroom12);
        hallway3.addClassroom(classroom13);
        hallway3.addClassroom(classroom14);
        hallway3.addClassroom(classroom15);
        hallway3.addClassroom(classroom16);
        hallway3.addClassroom(classroom17);
        hallway3.addClassroom(classroom18);
        hallway3.addClassroom(classroom19);
        hallway3.addClassroom(classroom20);
        hallway3.addClassroom(classroom21);
        hallway3.addClassroom(classroom22);
        hallway3.addClassroom(classroom23);

        Classroom classroom24 = new Classroom("119","право","первый");
        Classroom classroom25 = new Classroom("121","право","второй");
        Classroom classroom26 = new Classroom("120","лево","первый");
        Classroom classroom27 = new Classroom("123","лево","второй");
        Classroom classroom28 = new Classroom("124","лево","третий");
        classroom24.setHallway(hallway4);
        classroom25.setHallway(hallway4);
        classroom26.setHallway(hallway4);
        classroom27.setHallway(hallway4);
        classroom28.setHallway(hallway4);
        hallway4.addClassroom(classroom24);
        hallway4.addClassroom(classroom25);
        hallway4.addClassroom(classroom26);
        hallway4.addClassroom(classroom27);
        hallway4.addClassroom(classroom28);

        Classroom classroom29 = new Classroom("124а","лево","первый");
        Classroom classroom30 = new Classroom("124б","лево","второй");
        Classroom classroom31 = new Classroom("125","лево","третий");
        Classroom classroom32 = new Classroom("128","лево","четвертый");
        Classroom classroom33 = new Classroom("126","право","первый");
        Classroom classroom34 = new Classroom("127","право","второй");
        classroom29.setHallway(hallway5);
        classroom30.setHallway(hallway5);
        classroom31.setHallway(hallway5);
        classroom32.setHallway(hallway5);
        classroom33.setHallway(hallway5);
        classroom34.setHallway(hallway5);
        hallway5.addClassroom(classroom29);
        hallway5.addClassroom(classroom30);
        hallway5.addClassroom(classroom31);
        hallway5.addClassroom(classroom32);
        hallway5.addClassroom(classroom33);
        hallway5.addClassroom(classroom34);

        hallwaySevice.updateHallway(hallway1);
        hallwaySevice.updateHallway(hallway2);
        hallwaySevice.updateHallway(hallway3);
        hallwaySevice.updateHallway(hallway4);
        hallwaySevice.updateHallway(hallway5);
    }
    public void FillSecondFloor(){
        Area area1 = new Area("E",2,"A2");
        Area area2 = new Area("E",2,"Б2");
        Area area3 = new Area("E",2,"Л2");
        Area area4 = new Area("E",2,"В2");
        Area area5 = new Area("E",2,"Г2");
        areaService.saveArea(area1);
        areaService.saveArea(area2);
        areaService.saveArea(area3);
        areaService.saveArea(area4);
        areaService.saveArea(area5);

        Hallway hallway1 = new Hallway("лево","право");
        hallway1.setStartArea(area2);
        hallway1.setEndArea(area1);
        Hallway hallway2 = new Hallway("лево","право");
        hallway2.setStartArea(area3);
        hallway2.setEndArea(area2);
        Hallway hallway3 = new Hallway("лево","право");
        hallway3.setStartArea(area3);
        hallway3.setEndArea(area4);
        Hallway hallway4 = new Hallway("лево","право");
        hallway4.setStartArea(area4);
        hallway4.setEndArea(area5);
        List<Area> area6 = areaService.findByAreasName("Л1");
        Hallway hallway5 = new Hallway("лево","право");
        hallway5.setStartArea(area6.get(0));
        hallway5.setEndArea(area3);
        hallwaySevice.saveHallway(hallway1);
        hallwaySevice.saveHallway(hallway2);
        hallwaySevice.saveHallway(hallway3);
        hallwaySevice.saveHallway(hallway4);
        hallwaySevice.saveHallway(hallway5);

        Classroom classroom1 = new Classroom("205","лево","четвертый");
        Classroom classroom2 = new Classroom("204","лево","третий");
        Classroom classroom3 = new Classroom("203","лево","второй");
        Classroom classroom4 = new Classroom("202","лево","первый");
        classroom1.setHallway(hallway1);
        classroom2.setHallway(hallway1);
        classroom3.setHallway(hallway1);
        classroom4.setHallway(hallway1);
        hallway1.addClassroom(classroom1);
        hallway1.addClassroom(classroom2);
        hallway1.addClassroom(classroom3);
        hallway1.addClassroom(classroom4);

        Classroom classroom5 = new Classroom("217","лево","первый");
        Classroom classroom6 = new Classroom("215","лево","второй");
        Classroom classroom7 = new Classroom("213","лево","третий");
        Classroom classroom8 = new Classroom("211","лево","четвертый");
        Classroom classroom9 = new Classroom("210","лево","пятый");
        Classroom classroom10 = new Classroom("208","лево","шестой");
        Classroom classroom11 = new Classroom("200","лево","седьмой");
        Classroom classroom12 = new Classroom("201","лево","восьмой");
        Classroom classroom13 = new Classroom("218","право","первый");
        Classroom classroom14 = new Classroom("216","право","второй");
        Classroom classroom15 = new Classroom("214","право","третий");
        Classroom classroom16 = new Classroom("212","право","четвертый");
        Classroom classroom17 = new Classroom("209","право","пятый");
        Classroom classroom18 = new Classroom("207","право","шестой");
        Classroom classroom19 = new Classroom("206","право","седьмой");
        classroom5.setHallway(hallway2);
        classroom6.setHallway(hallway2);
        classroom7.setHallway(hallway2);
        classroom8.setHallway(hallway2);
        classroom9.setHallway(hallway2);
        classroom10.setHallway(hallway2);
        classroom11.setHallway(hallway2);
        classroom12.setHallway(hallway2);
        classroom13.setHallway(hallway2);
        classroom14.setHallway(hallway2);
        classroom15.setHallway(hallway2);
        classroom16.setHallway(hallway2);
        classroom17.setHallway(hallway2);
        classroom18.setHallway(hallway2);
        classroom19.setHallway(hallway2);
        hallway2.addClassroom(classroom5);
        hallway2.addClassroom(classroom6);
        hallway2.addClassroom(classroom7);
        hallway2.addClassroom(classroom8);
        hallway2.addClassroom(classroom9);
        hallway2.addClassroom(classroom10);
        hallway2.addClassroom(classroom11);
        hallway2.addClassroom(classroom12);
        hallway2.addClassroom(classroom13);
        hallway2.addClassroom(classroom14);
        hallway2.addClassroom(classroom15);
        hallway2.addClassroom(classroom16);
        hallway2.addClassroom(classroom17);
        hallway2.addClassroom(classroom18);
        hallway2.addClassroom(classroom19);

        Classroom classroom20 = new Classroom("220","право","первый");
        Classroom classroom21 = new Classroom("222","право","второй");
        Classroom classroom22 = new Classroom("224","право","третий");
        Classroom classroom23 = new Classroom("219","лево","первый");
        Classroom classroom24 = new Classroom("221","лево","второй");
        Classroom classroom25 = new Classroom("223","лево","третий");
        Classroom classroom26 = new Classroom("225","лево","четвертый");
        Classroom classroom27 = new Classroom("226","лево","пятый");
        Classroom classroom28 = new Classroom("226а","лево","шестой");
        classroom20.setHallway(hallway3);
        classroom21.setHallway(hallway3);
        classroom22.setHallway(hallway3);
        classroom23.setHallway(hallway3);
        classroom24.setHallway(hallway3);
        classroom25.setHallway(hallway3);
        classroom26.setHallway(hallway3);
        classroom27.setHallway(hallway3);
        classroom28.setHallway(hallway3);
        hallway3.addClassroom(classroom20);
        hallway3.addClassroom(classroom21);
        hallway3.addClassroom(classroom22);
        hallway3.addClassroom(classroom23);
        hallway3.addClassroom(classroom24);
        hallway3.addClassroom(classroom25);
        hallway3.addClassroom(classroom26);
        hallway3.addClassroom(classroom27);
        hallway3.addClassroom(classroom28);

        Classroom classroom29 = new Classroom("227","лево","первый");
        Classroom classroom30 = new Classroom("229","лево","второй");
        Classroom classroom31 = new Classroom("228","право","первый");
        Classroom classroom32 = new Classroom("230","право","второй");

        classroom29.setHallway(hallway4);
        classroom30.setHallway(hallway4);
        classroom31.setHallway(hallway4);
        classroom32.setHallway(hallway4);
        hallway4.addClassroom(classroom29);
        hallway4.addClassroom(classroom30);
        hallway4.addClassroom(classroom31);
        hallway4.addClassroom(classroom32);
        hallwaySevice.updateHallway(hallway1);
        hallwaySevice.updateHallway(hallway2);
        hallwaySevice.updateHallway(hallway3);
        hallwaySevice.updateHallway(hallway4);
    }
    public void FillThirdFloor(){
        Area area1 = new Area("E",3,"A3");
        Area area2 = new Area("E",3,"Б3");
        Area area3 = new Area("E",3,"Л3");
        Area area4 = new Area("E",3,"В3");
        Area area5 = new Area("E",3,"Г3");
        areaService.saveArea(area1);
        areaService.saveArea(area2);
        areaService.saveArea(area3);
        areaService.saveArea(area4);
        areaService.saveArea(area5);

        Hallway hallway1 = new Hallway("лево","право");
        hallway1.setStartArea(area2);
        hallway1.setEndArea(area1);
        Hallway hallway2 = new Hallway("лево","право");
        hallway2.setStartArea(area3);
        hallway2.setEndArea(area2);
        Hallway hallway3 = new Hallway("лево","право");
        hallway3.setStartArea(area3);
        hallway3.setEndArea(area4);
        Hallway hallway4 = new Hallway("лево","право");
        hallway4.setStartArea(area4);
        hallway4.setEndArea(area5);
        List<Area> area6 = areaService.findByAreasName("Л2");
        Hallway hallway5 = new Hallway("лево","право");
        hallway5.setStartArea(area6.get(0));
        hallway5.setEndArea(area3);
        hallwaySevice.saveHallway(hallway1);
        hallwaySevice.saveHallway(hallway2);
        hallwaySevice.saveHallway(hallway3);
        hallwaySevice.saveHallway(hallway4);
        hallwaySevice.saveHallway(hallway5);

        Classroom classroom1 = new Classroom("303а","лево","первый");
        Classroom classroom2 = new Classroom("303б","лево","второй");
        Classroom classroom3 = new Classroom("303","лево","третий");
        classroom1.setHallway(hallway1);
        classroom2.setHallway(hallway1);
        classroom3.setHallway(hallway1);
        hallway1.addClassroom(classroom1);
        hallway1.addClassroom(classroom2);
        hallway1.addClassroom(classroom3);

        Classroom classroom4 = new Classroom("313","лево","первый");
        Classroom classroom5 = new Classroom("310","лево","второй");
        Classroom classroom6 = new Classroom("308","лево","третий");
        Classroom classroom7 = new Classroom("307","лево","четвертый");
        Classroom classroom8 = new Classroom("305","лево","пятый");
        Classroom classroom9 = new Classroom("301","лево","шестой");
        Classroom classroom10 = new Classroom("317","право","первый");
        Classroom classroom11 = new Classroom("316","право","второй");
        Classroom classroom12 = new Classroom("315","право","третий");
        Classroom classroom13 = new Classroom("314","право","четвертый");
        Classroom classroom14 = new Classroom("312","право","пятый");
        Classroom classroom15 = new Classroom("311","право","шестой");
        Classroom classroom16 = new Classroom("309","право","седьмой");
        Classroom classroom17 = new Classroom("306","право","восьмой");
        Classroom classroom18 = new Classroom("304","право","девятый");
        classroom4.setHallway(hallway2);
        classroom5.setHallway(hallway2);
        classroom6.setHallway(hallway2);
        classroom7.setHallway(hallway2);
        classroom8.setHallway(hallway2);
        classroom9.setHallway(hallway2);
        classroom10.setHallway(hallway2);
        classroom11.setHallway(hallway2);
        classroom12.setHallway(hallway2);
        classroom13.setHallway(hallway2);
        classroom14.setHallway(hallway2);
        classroom15.setHallway(hallway2);
        classroom16.setHallway(hallway2);
        classroom17.setHallway(hallway2);
        classroom18.setHallway(hallway2);
        hallway2.addClassroom(classroom4);
        hallway2.addClassroom(classroom5);
        hallway2.addClassroom(classroom6);
        hallway2.addClassroom(classroom7);
        hallway2.addClassroom(classroom8);
        hallway2.addClassroom(classroom9);
        hallway2.addClassroom(classroom10);
        hallway2.addClassroom(classroom11);
        hallway2.addClassroom(classroom12);
        hallway2.addClassroom(classroom13);
        hallway2.addClassroom(classroom14);
        hallway2.addClassroom(classroom15);
        hallway2.addClassroom(classroom16);
        hallway2.addClassroom(classroom17);
        hallway2.addClassroom(classroom18);

        Classroom classroom19 = new Classroom("319","право","первый");
        Classroom classroom20 = new Classroom("321","право","второй");
        Classroom classroom21 = new Classroom("323","право","третий");
        Classroom classroom22 = new Classroom("318","лево","первый");
        Classroom classroom23 = new Classroom("320","лево","второй");
        Classroom classroom24 = new Classroom("322","лево","третий");
        Classroom classroom25 = new Classroom("324","лево","четвертый");
        Classroom classroom26 = new Classroom("325","лево","пятый");
        Classroom classroom27 = new Classroom("326","лево","шестой");
        classroom19.setHallway(hallway3);
        classroom20.setHallway(hallway3);
        classroom21.setHallway(hallway3);
        classroom22.setHallway(hallway3);
        classroom23.setHallway(hallway3);
        classroom24.setHallway(hallway3);
        classroom25.setHallway(hallway3);
        classroom26.setHallway(hallway3);
        classroom27.setHallway(hallway3);
        hallway3.addClassroom(classroom19);
        hallway3.addClassroom(classroom20);
        hallway3.addClassroom(classroom21);
        hallway3.addClassroom(classroom22);
        hallway3.addClassroom(classroom23);
        hallway3.addClassroom(classroom24);
        hallway3.addClassroom(classroom25);
        hallway3.addClassroom(classroom26);
        hallway3.addClassroom(classroom27);

        Classroom classroom28 = new Classroom("326а","лево","первый");
        Classroom classroom29 = new Classroom("328","лево","второй");
        Classroom classroom30 = new Classroom("330","лево","третий");
        Classroom classroom31 = new Classroom("332","лево","четвертый");
        Classroom classroom32 = new Classroom("327","право","первый");
        Classroom classroom33 = new Classroom("329","право","второй");
        Classroom classroom34 = new Classroom("331","право","третий");
        classroom28.setHallway(hallway4);
        classroom29.setHallway(hallway4);
        classroom30.setHallway(hallway4);
        classroom31.setHallway(hallway4);
        classroom32.setHallway(hallway4);
        classroom33.setHallway(hallway4);
        classroom34.setHallway(hallway4);
        hallway4.addClassroom(classroom28);
        hallway4.addClassroom(classroom29);
        hallway4.addClassroom(classroom30);
        hallway4.addClassroom(classroom31);
        hallway4.addClassroom(classroom32);
        hallway4.addClassroom(classroom33);
        hallway4.addClassroom(classroom34);
        hallwaySevice.updateHallway(hallway1);
        hallwaySevice.updateHallway(hallway2);
        hallwaySevice.updateHallway(hallway3);
        hallwaySevice.updateHallway(hallway4);
    }
    public void FillFourthFloor(){
        Area area1 = new Area("E",4,"A4");
        Area area2 = new Area("E",4,"Л4");
        Area area3 = new Area("E",4,"Б4");
        Area area4 = new Area("E",4,"В4");
        areaService.saveArea(area1);
        areaService.saveArea(area2);
        areaService.saveArea(area3);
        areaService.saveArea(area4);

        Hallway hallway1 = new Hallway("лево","право");
        hallway1.setStartArea(area2);
        hallway1.setEndArea(area1);
        Hallway hallway2 = new Hallway("лево","право");
        hallway2.setStartArea(area2);
        hallway2.setEndArea(area3);
        Hallway hallway3 = new Hallway("лево","право");
        hallway3.setStartArea(area3);
        hallway3.setEndArea(area4);
        List<Area> area5 = areaService.findByAreasName("Л3");
        Hallway hallway4 = new Hallway("лево","право");
        hallway4.setStartArea(area5.get(0));
        hallway4.setEndArea(area2);
        hallwaySevice.saveHallway(hallway1);
        hallwaySevice.saveHallway(hallway2);
        hallwaySevice.saveHallway(hallway3);
        hallwaySevice.saveHallway(hallway4);

        Classroom classroom1 = new Classroom("412","лево","первый");
        Classroom classroom2 = new Classroom("410","лево","второй");
        Classroom classroom3 = new Classroom("408","лево","третий");
        Classroom classroom4 = new Classroom("406","лево","четвертый");
        Classroom classroom5 = new Classroom("404","лево","пятый");
        Classroom classroom6 = new Classroom("401а","лево","шестой");
        Classroom classroom7 = new Classroom("401","лево","седьмой");
        Classroom classroom8 = new Classroom("416","право","первый");
        Classroom classroom9 = new Classroom("415","право","второй");
        Classroom classroom10 = new Classroom("414","право","третий");
        Classroom classroom11 = new Classroom("413","право","четвертый");
        Classroom classroom12 = new Classroom("411","право","пятый");
        Classroom classroom13 = new Classroom("409","право","шестой");
        Classroom classroom14 = new Classroom("407","право","седьмой");
        Classroom classroom15 = new Classroom("405","право","восьмой");
        Classroom classroom16 = new Classroom("403","право","девятый");
        Classroom classroom17 = new Classroom("402","право","десятый");
        classroom1.setHallway(hallway1);
        classroom2.setHallway(hallway1);
        classroom3.setHallway(hallway1);
        classroom4.setHallway(hallway1);
        classroom5.setHallway(hallway1);
        classroom6.setHallway(hallway1);
        classroom7.setHallway(hallway1);
        classroom8.setHallway(hallway1);
        classroom9.setHallway(hallway1);
        classroom10.setHallway(hallway1);
        classroom11.setHallway(hallway1);
        classroom12.setHallway(hallway1);
        classroom13.setHallway(hallway1);
        classroom14.setHallway(hallway1);
        classroom15.setHallway(hallway1);
        classroom16.setHallway(hallway1);
        classroom17.setHallway(hallway1);
        hallway1.addClassroom(classroom1);
        hallway1.addClassroom(classroom2);
        hallway1.addClassroom(classroom3);
        hallway1.addClassroom(classroom4);
        hallway1.addClassroom(classroom5);
        hallway1.addClassroom(classroom6);
        hallway1.addClassroom(classroom7);
        hallway1.addClassroom(classroom8);
        hallway1.addClassroom(classroom9);
        hallway1.addClassroom(classroom10);
        hallway1.addClassroom(classroom11);
        hallway1.addClassroom(classroom12);
        hallway1.addClassroom(classroom13);
        hallway1.addClassroom(classroom14);
        hallway1.addClassroom(classroom15);
        hallway1.addClassroom(classroom16);
        hallway1.addClassroom(classroom17);

        Classroom classroom18 = new Classroom("418","право","первый");
        Classroom classroom19 = new Classroom("419","право","второй");
        Classroom classroom20 = new Classroom("420","право","третий");
        Classroom classroom21 = new Classroom("423","право","четвертый");
        Classroom classroom22 = new Classroom("424","право","пятый");
        Classroom classroom23 = new Classroom("417","лево","первый");
        Classroom classroom24 = new Classroom("421","лево","второй");
        Classroom classroom25 = new Classroom("422а","лево","третий");
        Classroom classroom26 = new Classroom("422","лево","четвертый");
        Classroom classroom27 = new Classroom("425","лево","пятый");
        classroom18.setHallway(hallway2);
        classroom19.setHallway(hallway2);
        classroom20.setHallway(hallway2);
        classroom21.setHallway(hallway2);
        classroom22.setHallway(hallway2);
        classroom23.setHallway(hallway2);
        classroom24.setHallway(hallway2);
        classroom25.setHallway(hallway2);
        classroom26.setHallway(hallway2);
        classroom27.setHallway(hallway2);
        hallway2.addClassroom(classroom18);
        hallway2.addClassroom(classroom19);
        hallway2.addClassroom(classroom20);
        hallway2.addClassroom(classroom21);
        hallway2.addClassroom(classroom22);
        hallway2.addClassroom(classroom23);
        hallway2.addClassroom(classroom24);
        hallway2.addClassroom(classroom25);
        hallway2.addClassroom(classroom26);
        hallway2.addClassroom(classroom27);

        Classroom classroom28 = new Classroom("427","лево","первый");
        Classroom classroom29 = new Classroom("429","лево","второй");
        Classroom classroom30 = new Classroom("430","лево","третий");
        Classroom classroom31 = new Classroom("426","право","первый");
        Classroom classroom32 = new Classroom("428","право","второй");
        classroom28.setHallway(hallway3);
        classroom29.setHallway(hallway3);
        classroom30.setHallway(hallway3);
        classroom31.setHallway(hallway3);
        classroom32.setHallway(hallway3);
        hallway3.addClassroom(classroom28);
        hallway3.addClassroom(classroom29);
        hallway3.addClassroom(classroom30);
        hallway3.addClassroom(classroom31);
        hallway3.addClassroom(classroom32);
        hallwaySevice.updateHallway(hallway1);
        hallwaySevice.updateHallway(hallway2);
        hallwaySevice.updateHallway(hallway3);
    }
    public ResponseData getSchedule(String facultet, String direction, String group) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "getfaculties");

        String facultiesJSON = doPostQuery(getUrl(), params); // список всех факультетов
        JSONArray jObj = new JSONArray(facultiesJSON);
        String post_id = null;
        for (int i = 0; i < jObj.length(); i++){
            if (jObj.getJSONObject(i).getString("title").equals(facultet)) {
                post_id = jObj.getJSONObject(i).getString("id");
                break;
            }
        }
        if (post_id == null)
            return null;
        //--------------------------------

        params = new HashMap<String, String>();
        params.put("action", "getbranches");
        params.put("id", post_id);

        facultiesJSON = doPostQuery(getUrl(), params); // получаем все направления подготовки
        jObj = new JSONArray(facultiesJSON);
        post_id = null;
        for (int i = 0; i < jObj.length(); i++){
            if (jObj.getJSONObject(i).getString("title").equals(direction)) {
                post_id = jObj.getJSONObject(i).getString("id");
                break;
            }
        }
        if (post_id == null)
            return null;
        //---------------------------------
        params.clear();
        params = new HashMap<String, String>();
        params.put("action", "getgroups");
        params.put("id", post_id);

        facultiesJSON = doPostQuery(getUrl(), params); // Все группы
        jObj = new JSONArray(facultiesJSON);
        post_id = null;
        for (int i = 0; i < jObj.length(); i++){
            if (jObj.getJSONObject(i).getString("title").equals(group)) {
                post_id = jObj.getJSONObject(i).getString("id");
                break;
            }
        }
        if (post_id == null)
            return null;
        //----------------------------------
        params.clear();
        params = new HashMap<String, String>();
        params.put("action", "gettimetable");
        params.put("mode", "student");
        params.put("id", post_id);

        facultiesJSON = doPostQuery(getUrl(), params); // Расписание для группы
        jObj = new JSONArray(facultiesJSON);

        if (jObj.length() == 0)
            return null;
        else
        {
            // Записываем расписание для группы в БД
            studentsService.saveStudent(new Students(facultet, direction, group, jObj.toString()));
            //---------------------------------------------
            ResponseData l = new ResponseData();
            l.setGroupName(group);
            l.setSchedule(jObj.toString());
            return l;
        }
    }
    private static String doPostQuery(String url, Map<String, String> params) throws IOException {
        URL buildingUrl = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) buildingUrl.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
        for (Map.Entry<String, String> entry: params.entrySet()) {
            out.writeBytes(URLEncoder.encode(entry.getKey(), "UTF-8"));
            out.writeBytes("=");
            out.writeBytes(URLEncoder.encode(entry.getValue(), "UTF-8"));
            out.writeBytes("&");
        }
        out.flush();
        out.close();
        httpURLConnection.setConnectTimeout(1000);
        httpURLConnection.setReadTimeout(1000);

        int status = httpURLConnection.getResponseCode();
        if (status != 200){
            throw new IOException("status is not 200 OK");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null){
            content.append(inputLine);
        }
        in.close();
        httpURLConnection.disconnect();
        return content.toString();
    }
}
