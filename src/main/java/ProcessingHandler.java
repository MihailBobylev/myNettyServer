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
        //ResultSet rs;
        //ResponseData responseData = new ResponseData();
        // Поиск данных в БД
        // case по параметру (0, 1, 2, 3, 4), опредлеляющему запрос пользователя
        ResponseData responseData = new ResponseData();

        int flag = 1;
        switch (flag){
            case 0: // расписание аудитории
                audsSchedule = audsService.findByAud(requestData.getCorp(),requestData.getAuditor());
                allLessons =  lessonService.findByAudID(audsSchedule.get(0), requestData.getWeek(),requestData.getDayOfWeek(),requestData.getLessonNumber());
                if (audsSchedule.size() != 0){
                    responseData.setTeacherName(allLessons.get(0).getTeacher().getName());
                    responseData.setLessonName(allLessons.get(0).getLessonByGroup());
                    responseData.setLessonType(allLessons.get(0).getLessontype());
                    responseData.setGroupName(allLessons.get(0).getSubgroup());
                }else
                {
                    responseData.setTeacherName("Неизвестно");
                    responseData.setLessonName("Неизвестно");
                    responseData.setLessonType("Неизвестно");
                    responseData.setGroupName("Неизвестно");
                }
                break;
            case 1: // расписание препода
                //System.out.println(requestData.getTeacherName());
                teacherSchedule =  teachersService.findTeacherByName(requestData.getTeacherName());
                allLessons =  lessonService.findByTeacherID(teacherSchedule.get(0));
                String lessonNumber = "";
                String corp = "";
                String auditor = "";
                String lessonName = "";
                String lessonType = "";
                String groupName = "";
                Auds auds2;
                if (allLessons.size() == 0){
                    responseData.setLessonNumber("Неизвестно");
                    responseData.setCorps("Неизвестно");
                    responseData.setAuditor("Неизвестно");
                    responseData.setLessonName("Неизвестно");
                    responseData.setLessonType("Неизвестно");
                    responseData.setGroupName("Неизвестно");
                    break;
                }
                for (Lesson l: allLessons) {
                    auds2 = audsService.findAuds(l.getAud().getId());
                    lessonNumber += l.getNumberOfClass() + "@";
                    corp += auds2.getCorp() + "@";
                    auditor += auds2.getNumber() + "@";
                    lessonName += l.getLessonByGroup() + "@";
                    lessonType += l.getLessontype() + "@";
                    groupName += l.getSubgroup() + "@";
                }
                responseData.setLessonNumber(lessonNumber);
                responseData.setCorps(corp);
                responseData.setAuditor(auditor);
                responseData.setLessonName(lessonName);
                responseData.setLessonType(lessonType);
                responseData.setGroupName(groupName);

                //Заполнение оставшихся полей
                responseData.setTeacherName("Препод");
                responseData.setWays("Путь");
                responseData.setInstitute("Институт");
                responseData.setDirection("Направление");

                break;
            case 2: // расписание группы
                allLessons = lessonService.findByGroup(requestData.getGroup());
                String lessonNumber2 = "";
                String corp2 = "";
                String auditor2 = "";
                String lessonName2 = "";
                String lessonType2 = "";
                String teacher2 = "";
                if (allLessons.size() == 0){
                    responseData.setLessonNumber("Неизвестно");
                    responseData.setCorps("Неизвестно");
                    responseData.setAuditor("Неизвестно");
                    responseData.setLessonName("Неизвестно");
                    responseData.setLessonType("Неизвестно");
                    responseData.setTeacherName("Неизвестно");
                    break;
                }
                for (Lesson l: allLessons) {
                    Auds auds = audsService.findAuds(l.getId());
                    lessonNumber2 += l.getNumberOfClass() + "@";
                    corp2 += auds.getCorp() + "@";
                    auditor2 += auds.getNumber() + "@";
                    lessonName2 += l.getLessonByGroup() + "@";
                    lessonType2 += l.getLessontype() + "@";
                    teacher2 += l.getTeacher().getName() + "@";
                }
                responseData.setLessonNumber(lessonNumber2);
                responseData.setCorps(corp2);
                responseData.setAuditor(auditor2);
                responseData.setLessonName(lessonName2);
                responseData.setLessonType(lessonType2);
                responseData.setTeacherName(teacher2);

                //groupSchedule.clear();
                break;
            case 3: // поиск пути
                break;
            case 4: // заполнение БД клиента
                groupSchedule = studentsService.findAllStudents();// институт, направление, группа
                String institute = "";
                String direction = "";
                String groupp = "";
                for (Students s: groupSchedule) {
                    institute += s.getInstitute() + "@";
                    direction += s.getDirection() + "@";
                    groupp += s.getGroupp() + "@";
                }
                responseData.setInstitute(institute);
                responseData.setDirection(direction);
                requestData.setGroup(groupp);

                teacherSchedule = teachersService.findAllTeachers();// имя преподавателя
                String teachName = "";
                for (Teachers t: teacherSchedule) {
                    teachName += t.getName() + "@";
                }
                responseData.setTeacherName(teachName);

                audsSchedule = audsService.findAllAuds();// корпуса и аудитории
                String corp3 = "";
                String audName = "";
                for (Auds a: audsSchedule) {
                    corp3 += a.getCorp() + "@";
                    audName += a.getNumber() + "@";
                }
                responseData.setCorps(corp3);
                responseData.setAuditor(audName);
                break;
        }
        System.out.println("Что-то вернул1");
        ChannelFuture future = ctx.writeAndFlush(responseData); //responseData
        future.addListener(ChannelFutureListener.CLOSE);
        System.out.println("Что-то вернул2");
        //System.out.println(requestData.getTeacherName());
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
            //l.setClassName(className);
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

        Hallway hallway1 = new Hallway("На развилке повернуть направо","Встать спиной к тупику, пойти прямо, на развилке повернуть налево");
        hallway1.setStartArea(area2);
        hallway1.setEndArea(area1);
        Hallway hallway2 = new Hallway("На развилке повернуть налево","Встать спиной к тупику, пойти прямо, на развилке повернуть направо");
        hallway2.setStartArea(area2);
        hallway2.setEndArea(area3);
        Hallway hallway3 = new Hallway("Повернуть налево от главного входа и идти прямо","Идти по направлению к главному входу");
        hallway3.setStartArea(area4);
        hallway3.setEndArea(area2);
        Hallway hallway4 = new Hallway("Повернуть направо от главного входа и идти прямо","Идти по направлению к главному входу");
        hallway4.setStartArea(area4);
        hallway4.setEndArea(area5);
        Hallway hallway5 = new Hallway("В конце повернуть направо","Встать спиной к тупику, пойти прямо, в конце повернуть налево");
        hallway5.setStartArea(area5);
        hallway5.setEndArea(area6);
        hallwaySevice.saveHallway(hallway1);
        hallwaySevice.saveHallway(hallway2);
        hallwaySevice.saveHallway(hallway3);
        hallwaySevice.saveHallway(hallway4);
        hallwaySevice.saveHallway(hallway5);

        Classroom classroom1 = new Classroom("101","По левой стороне","второй кабинет");
        Classroom classroom2 = new Classroom("102","По правой стороне","четвертый кабинет");
        Classroom classroom3 = new Classroom("103","По правой стороне","третий кабинет");
        Classroom classroom4 = new Classroom("104","По левой стороне","первый кабинет");
        Classroom classroom5 = new Classroom("105","По правой стороне","второй кабинет");
        Classroom classroom6 = new Classroom("105а","По правой стороне","первый кабинет");
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

        Classroom classroom7 = new Classroom("106","По левой стороне","первый кабинет");
        Classroom classroom8 = new Classroom("107","По левой стороне","второй кабинет");
        Classroom classroom9 = new Classroom("108","По левой стороне","третий кабинет");
        Classroom classroom10 = new Classroom("109","По левой стороне","четвертый кабинет");
        classroom7.setHallway(hallway1);
        classroom8.setHallway(hallway1);
        classroom9.setHallway(hallway1);
        classroom10.setHallway(hallway1);
        hallway1.addClassroom(classroom7);
        hallway1.addClassroom(classroom8);
        hallway1.addClassroom(classroom9);
        hallway1.addClassroom(classroom10);

        Classroom classroom11 = new Classroom("100","По правой стороне","первый кабинет");
        Classroom classroom12 = new Classroom("99","По правой стороне","второй кабинет");
        Classroom classroom13 = new Classroom("115","По правой стороне","третий кабинет");
        Classroom classroom14 = new Classroom("113","По правой стороне","четвертый кабинет");
        Classroom classroom15 = new Classroom("113а","По правой стороне","четвертый кабинет");
        Classroom classroom16 = new Classroom("111","По правой стороне","пятый кабинет");
        Classroom classroom17 = new Classroom("110","По правой стороне","шестой кабинет");
        Classroom classroom18 = new Classroom("97","По правой стороне","седьмой кабинет");
        Classroom classroom19 = new Classroom("118","По левой стороне","первый кабинет");
        Classroom classroom20 = new Classroom("117","По левой стороне","второй кабинет");
        Classroom classroom21 = new Classroom("116","По левой стороне","третий кабинет");
        Classroom classroom22 = new Classroom("114","По левой стороне","четвертый кабинет");
        Classroom classroom23 = new Classroom("112","По левой стороне","пятый кабинет");
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

        Classroom classroom24 = new Classroom("119","По правой стороне","первый кабинет");
        Classroom classroom25 = new Classroom("121","По правой стороне","второй кабинет");
        Classroom classroom26 = new Classroom("120","По левой стороне","первый кабинет");
        Classroom classroom27 = new Classroom("123","По левой стороне","второй кабинет");
        Classroom classroom28 = new Classroom("124","По левой стороне","третий кабинет");
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

        Classroom classroom29 = new Classroom("124а","По левой стороне","первый кабинет");
        Classroom classroom30 = new Classroom("124б","По левой стороне","второй кабинет");
        Classroom classroom31 = new Classroom("125","По левой стороне","третий кабинет");
        Classroom classroom32 = new Classroom("128","По левой стороне","четвертый кабинет");
        Classroom classroom33 = new Classroom("126","По правой стороне","первый кабинет");
        Classroom classroom34 = new Classroom("127","По правой стороне","второй кабинет");
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

        Hallway hallway1 = new Hallway("Повернуть направо и идти прямо","Встать спиной к тупику, пойти прямо и в конце повернуть налево");
        hallway1.setStartArea(area2);
        hallway1.setEndArea(area1);
        Hallway hallway2 = new Hallway("От лесницы повернуть налево и идти прямо","Идти по направлению к леснице");
        hallway2.setStartArea(area3);
        hallway2.setEndArea(area2);
        Hallway hallway3 = new Hallway("От лесницы повернуть направо и идти прямо","Идти по направлению к леснице");
        hallway3.setStartArea(area3);
        hallway3.setEndArea(area4);
        Hallway hallway4 = new Hallway("Повернуть направо и идти прямо","Встать спиной к тупику, пойти прямо и в конце повернуть налево");
        hallway4.setStartArea(area4);
        hallway4.setEndArea(area5);
        List<Area> area6 = areaService.findByAreasName("Л1");
        Hallway hallway5 = new Hallway("Подняться на второй этаж","Спуститься на первый этаж");
        hallway5.setStartArea(area6.get(0));
        hallway5.setEndArea(area3);
        hallwaySevice.saveHallway(hallway1);
        hallwaySevice.saveHallway(hallway2);
        hallwaySevice.saveHallway(hallway3);
        hallwaySevice.saveHallway(hallway4);
        hallwaySevice.saveHallway(hallway5);

        Classroom classroom1 = new Classroom("205","По левой стороне","четвертый кабинет");
        Classroom classroom2 = new Classroom("204","По левой стороне","третий кабинет");
        Classroom classroom3 = new Classroom("203","По левой стороне","второй кабинет");
        Classroom classroom4 = new Classroom("202","По левой стороне","первый кабинет");
        classroom1.setHallway(hallway1);
        classroom2.setHallway(hallway1);
        classroom3.setHallway(hallway1);
        classroom4.setHallway(hallway1);
        hallway1.addClassroom(classroom1);
        hallway1.addClassroom(classroom2);
        hallway1.addClassroom(classroom3);
        hallway1.addClassroom(classroom4);

        Classroom classroom5 = new Classroom("217","По левой стороне","первый кабинет");
        Classroom classroom6 = new Classroom("215","По левой стороне","второй кабинет");
        Classroom classroom7 = new Classroom("213","По левой стороне","третий кабинет");
        Classroom classroom8 = new Classroom("211","По левой стороне","четвертый кабинет");
        Classroom classroom9 = new Classroom("210","По левой стороне","пятый кабинет");
        Classroom classroom10 = new Classroom("208","По левой стороне","шестой кабинет");
        Classroom classroom11 = new Classroom("200","По левой стороне","седьмой кабинет");
        Classroom classroom12 = new Classroom("201","По левой стороне","восьмой кабинет");
        Classroom classroom13 = new Classroom("218","По правой стороне","первый кабинет");
        Classroom classroom14 = new Classroom("216","По правой стороне","второй кабинет");
        Classroom classroom15 = new Classroom("214","По правой стороне","третий кабинет");
        Classroom classroom16 = new Classroom("212","По правой стороне","четвертый кабинет");
        Classroom classroom17 = new Classroom("209","По правой стороне","пятый кабинет");
        Classroom classroom18 = new Classroom("207","По правой стороне","шестой кабинет");
        Classroom classroom19 = new Classroom("206","По правой стороне","седьмой кабинет");
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

        Classroom classroom20 = new Classroom("220","По правой стороне","первый кабинет");
        Classroom classroom21 = new Classroom("222","По правой стороне","второй кабинет");
        Classroom classroom22 = new Classroom("224","По правой стороне","третий кабинет");
        Classroom classroom23 = new Classroom("219","По левой стороне","первый кабинет");
        Classroom classroom24 = new Classroom("221","По левой стороне","второй кабинет");
        Classroom classroom25 = new Classroom("223","По левой стороне","третий кабинет");
        Classroom classroom26 = new Classroom("225","По левой стороне","четвертый кабинет");
        Classroom classroom27 = new Classroom("226","По левой стороне","пятый кабинет");
        Classroom classroom28 = new Classroom("226а","По левой стороне","шестой кабинет");
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

        Classroom classroom29 = new Classroom("227","По левой стороне","первый кабинет");
        Classroom classroom30 = new Classroom("229","По левой стороне","второй кабинет");
        Classroom classroom31 = new Classroom("228","По правой стороне","первый кабинет");
        Classroom classroom32 = new Classroom("230","По правой стороне","второй кабинет");

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

        Hallway hallway1 = new Hallway("Повернуть направо и идти прямо","Встать спиной к тупику, пойти прямо и в конце повернуть налево");
        hallway1.setStartArea(area2);
        hallway1.setEndArea(area1);
        Hallway hallway2 = new Hallway("От лесницы повернуть налево и идти прямо","Идти по направлению к леснице");
        hallway2.setStartArea(area3);
        hallway2.setEndArea(area2);
        Hallway hallway3 = new Hallway("От лесницы повернуть направо и идти прямо","Идти по направлению к леснице");
        hallway3.setStartArea(area3);
        hallway3.setEndArea(area4);
        Hallway hallway4 = new Hallway("Повернуть направо и идти прямо","Встать спиной к тупику, пойти прямо и в конце повернуть налево");
        hallway4.setStartArea(area4);
        hallway4.setEndArea(area5);
        List<Area> area6 = areaService.findByAreasName("Л2");
        Hallway hallway5 = new Hallway("Подняться на третий этаж","Спуститься на второй этаж");
        hallway5.setStartArea(area6.get(0));
        hallway5.setEndArea(area3);
        hallwaySevice.saveHallway(hallway1);
        hallwaySevice.saveHallway(hallway2);
        hallwaySevice.saveHallway(hallway3);
        hallwaySevice.saveHallway(hallway4);
        hallwaySevice.saveHallway(hallway5);

        Classroom classroom1 = new Classroom("303а","По левой стороне","первый кабинет");
        Classroom classroom2 = new Classroom("303б","По левой стороне","второй кабинет");
        Classroom classroom3 = new Classroom("303","По левой стороне","третий кабинет");
        classroom1.setHallway(hallway1);
        classroom2.setHallway(hallway1);
        classroom3.setHallway(hallway1);
        hallway1.addClassroom(classroom1);
        hallway1.addClassroom(classroom2);
        hallway1.addClassroom(classroom3);

        Classroom classroom4 = new Classroom("313","По левой стороне","первый кабинет");
        Classroom classroom5 = new Classroom("310","По левой стороне","второй кабинет");
        Classroom classroom6 = new Classroom("308","По левой стороне","третий кабинет");
        Classroom classroom7 = new Classroom("307","По левой стороне","четвертый кабинет");
        Classroom classroom8 = new Classroom("305","По левой стороне","пятый кабинет");
        Classroom classroom9 = new Classroom("301","По левой стороне","шестой кабинет");
        Classroom classroom10 = new Classroom("317","По правой стороне","первый кабинет");
        Classroom classroom11 = new Classroom("316","По правой стороне","второй кабинет");
        Classroom classroom12 = new Classroom("315","По правой стороне","третий кабинет");
        Classroom classroom13 = new Classroom("314","По правой стороне","четвертый кабинет");
        Classroom classroom14 = new Classroom("312","По правой стороне","пятый кабинет");
        Classroom classroom15 = new Classroom("311","По правой стороне","шестой кабинет");
        Classroom classroom16 = new Classroom("309","По правой стороне","седьмой кабинет");
        Classroom classroom17 = new Classroom("306","По правой стороне","восьмой кабинет");
        Classroom classroom18 = new Classroom("304","По правой стороне","девятый кабинет");
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

        Classroom classroom19 = new Classroom("319","По правой стороне","первый кабинет");
        Classroom classroom20 = new Classroom("321","По правой стороне","второй кабинет");
        Classroom classroom21 = new Classroom("323","По правой стороне","третий кабинет");
        Classroom classroom22 = new Classroom("318","По левой стороне","первый кабинет");
        Classroom classroom23 = new Classroom("320","По левой стороне","второй кабинет");
        Classroom classroom24 = new Classroom("322","По левой стороне","третий кабинет");
        Classroom classroom25 = new Classroom("324","По левой стороне","четвертый кабинет");
        Classroom classroom26 = new Classroom("325","По левой стороне","пятый кабинет");
        Classroom classroom27 = new Classroom("326","По левой стороне","шестой кабинет");
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

        Classroom classroom28 = new Classroom("326а","По левой стороне","первый кабинет");
        Classroom classroom29 = new Classroom("328","По левой стороне","второй кабинет");
        Classroom classroom30 = new Classroom("330","По левой стороне","третий кабинет");
        Classroom classroom31 = new Classroom("332","По левой стороне","четвертый кабинет");
        Classroom classroom32 = new Classroom("327","По правой стороне","первый кабинет");
        Classroom classroom33 = new Classroom("329","По правой стороне","второй кабинет");
        Classroom classroom34 = new Classroom("331","По правой стороне","третий кабинет");
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

        Hallway hallway1 = new Hallway("От лесницы повернуть налево и идти прямо","Идти по направлению к леснице");
        hallway1.setStartArea(area2);
        hallway1.setEndArea(area1);
        Hallway hallway2 = new Hallway("От лесницы повернуть направо и идти прямо","Идти по направлению к леснице");
        hallway2.setStartArea(area2);
        hallway2.setEndArea(area3);
        Hallway hallway3 = new Hallway("Повернуть направо и идти прямо","Встать спиной к тупику, пойти прямо и в конце повернуть налево");
        hallway3.setStartArea(area3);
        hallway3.setEndArea(area4);
        List<Area> area5 = areaService.findByAreasName("Л3");
        Hallway hallway4 = new Hallway("Подняться на четвертый этаж","Спуститься на третий этаж");
        hallway4.setStartArea(area5.get(0));
        hallway4.setEndArea(area2);
        hallwaySevice.saveHallway(hallway1);
        hallwaySevice.saveHallway(hallway2);
        hallwaySevice.saveHallway(hallway3);
        hallwaySevice.saveHallway(hallway4);

        Classroom classroom1 = new Classroom("412","По левой стороне","первый кабинет");
        Classroom classroom2 = new Classroom("410","По левой стороне","второй кабинет");
        Classroom classroom3 = new Classroom("408","По левой стороне","третий кабинет");
        Classroom classroom4 = new Classroom("406","По левой стороне","четвертый кабинет");
        Classroom classroom5 = new Classroom("404","По левой стороне","пятый кабинет");
        Classroom classroom6 = new Classroom("401а","По левой стороне","шестой кабинет");
        Classroom classroom7 = new Classroom("401","По левой стороне","седьмой кабинет");
        Classroom classroom8 = new Classroom("416","По правой стороне","первый кабинет");
        Classroom classroom9 = new Classroom("415","По правой стороне","второй кабинет");
        Classroom classroom10 = new Classroom("414","По правой стороне","третий кабинет");
        Classroom classroom11 = new Classroom("413","По правой стороне","четвертый кабинет");
        Classroom classroom12 = new Classroom("411","По правой стороне","пятый кабинет");
        Classroom classroom13 = new Classroom("409","По правой стороне","шестой кабинет");
        Classroom classroom14 = new Classroom("407","По правой стороне","седьмой кабинет");
        Classroom classroom15 = new Classroom("405","По правой стороне","восьмой кабинет");
        Classroom classroom16 = new Classroom("403","По правой стороне","девятый кабинет");
        Classroom classroom17 = new Classroom("402","По правой стороне","десятый кабинет");
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

        Classroom classroom18 = new Classroom("418","По правой стороне","первый кабинет");
        Classroom classroom19 = new Classroom("419","По правой стороне","второй кабинет");
        Classroom classroom20 = new Classroom("420","По правой стороне","третий кабинет");
        Classroom classroom21 = new Classroom("423","По правой стороне","четвертый кабинет");
        Classroom classroom22 = new Classroom("424","По правой стороне","пятый кабинет");
        Classroom classroom23 = new Classroom("417","По левой стороне","первый кабинет");
        Classroom classroom24 = new Classroom("421","По левой стороне","второй кабинет");
        Classroom classroom25 = new Classroom("422а","По левой стороне","третий кабинет");
        Classroom classroom26 = new Classroom("422","По левой стороне","четвертый кабинет");
        Classroom classroom27 = new Classroom("425","По левой стороне","пятый кабинет");
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

        Classroom classroom28 = new Classroom("427","По левой стороне","первый кабинет");
        Classroom classroom29 = new Classroom("429","По левой стороне","второй кабинет");
        Classroom classroom30 = new Classroom("430","По левой стороне","третий кабинет");
        Classroom classroom31 = new Classroom("426","По правой стороне","первый кабинет");
        Classroom classroom32 = new Classroom("428","По правой стороне","второй кабинет");
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
    public void FillFifthFloor(){
        Area area1 = new Area("E",5,"A5");
        Area area2 = new Area("E",5,"Л5");
        Area area3 = new Area("E",5,"Б5");
        areaService.saveArea(area1);
        areaService.saveArea(area2);
        areaService.saveArea(area3);

        Hallway hallway1 = new Hallway("От лесницы повернуть налево и идти прямо","Идти по направлению к леснице");
        hallway1.setStartArea(area2);
        hallway1.setEndArea(area1);
        Hallway hallway2 = new Hallway("От лесницы повернуть направо и идти прямо","Идти по направлению к леснице");
        hallway2.setStartArea(area2);
        hallway2.setEndArea(area3);
        List<Area> area4 = areaService.findByAreasName("Л4");
        Hallway hallway3 = new Hallway("Подняться на пятый этаж","Спуститься на четвертый этаж");
        hallway3.setStartArea(area4.get(0));
        hallway3.setEndArea(area2);
        hallwaySevice.saveHallway(hallway1);
        hallwaySevice.saveHallway(hallway2);
        hallwaySevice.saveHallway(hallway3);

        Classroom classroom1 = new Classroom("515","По левой стороне","первый кабинет");
        Classroom classroom2 = new Classroom("513","По левой стороне","второй кабинет");
        Classroom classroom3 = new Classroom("512","По левой стороне","третий кабинет");
        Classroom classroom4 = new Classroom("511","По левой стороне","четвертый кабинет");
        Classroom classroom5 = new Classroom("509","По левой стороне","пятый кабинет");
        Classroom classroom6 = new Classroom("504","По левой стороне","шестой кабинет");
        Classroom classroom7 = new Classroom("501","По левой стороне","седьмой кабинет");
        Classroom classroom8 = new Classroom("501б","По левой стороне","восьмой кабинет");
        Classroom classroom9 = new Classroom("516а","По правой стороне","первый кабинет");
        Classroom classroom10 = new Classroom("514","По правой стороне","второй кабинет");
        Classroom classroom11 = new Classroom("510","По правой стороне","третий кабинет");
        Classroom classroom12 = new Classroom("508","По правой стороне","четвертый кабинет");
        Classroom classroom13 = new Classroom("507","По правой стороне","пятый кабинет");
        Classroom classroom14 = new Classroom("505","По правой стороне","шестой кабинет");
        Classroom classroom15 = new Classroom("503","По правой стороне","седьмой кабинет");
        Classroom classroom16 = new Classroom("502","По правой стороне","восьмой кабинет");
        Classroom classroom17 = new Classroom("501а","По правой стороне","девятый кабинет");
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

        Classroom classroom18 = new Classroom("518","По правой стороне","первый кабинет");
        Classroom classroom19 = new Classroom("519","По правой стороне","второй кабинет");
        Classroom classroom20 = new Classroom("519а","По правой стороне","третий кабинет");
        Classroom classroom21 = new Classroom("521","По правой стороне","четвертый кабинет");
        Classroom classroom28 = new Classroom("523","По правой стороне","пятый кабинет");
        Classroom classroom29 = new Classroom("530","По правой стороне","пятый кабинет");
        Classroom classroom22 = new Classroom("516","По левой стороне","первый кабинет");
        Classroom classroom23 = new Classroom("517","По левой стороне","второй кабинет");
        Classroom classroom24 = new Classroom("520","По левой стороне","третий кабинет");
        Classroom classroom25 = new Classroom("521б","По левой стороне","третий кабинет");
        Classroom classroom26 = new Classroom("525","По левой стороне","четвертый кабинет");
        Classroom classroom27 = new Classroom("522","По левой стороне","пятый кабинет");

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
        classroom28.setHallway(hallway2);
        classroom29.setHallway(hallway2);
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
        hallway2.addClassroom(classroom28);
        hallway2.addClassroom(classroom29);

        hallwaySevice.updateHallway(hallway1);
        hallwaySevice.updateHallway(hallway2);
    }

    public static List<String> buildRoute(String startRoomNumber, String endRoomNumber) throws Exception { //String corp,
        List<String> route = new ArrayList<>();
        HallwaySevice hallwaySevice = new HallwaySevice();
        Classroom startRoom = hallwaySevice.getClassroom(startRoomNumber); //corp,
        Hallway startHallway = startRoom.getHallway();
        Classroom endRoom = hallwaySevice.getClassroom(endRoomNumber); //corp,
        Hallway endHallway = endRoom.getHallway();
        List<Hallway> hallways = pathFind(startHallway.getStartArea().getId(), endHallway.getEndArea().getId()); //, endHallway.getStartArea().getId()
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
    public static List<Hallway> pathFind(Integer startId, Integer endId) throws Exception {
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
            //l.setSchedule(jObj.toString());
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
