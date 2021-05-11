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
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "getbuildings");

        String buildingsJSON = doPostQuery(getUrl(), params);
        JSONArray jObj = new JSONArray(buildingsJSON); // список всех корпусов
        JSONArray jAuds;
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
                    params.clear();
                    params.put("action", "gettimetable");
                    params.put("mode", "student");
                    params.put("id", jGroups.getJSONObject(x).getString("id"));
                    facultiesJSON = doPostQuery(getUrl(), params);
                    jShedule = new JSONArray(facultiesJSON); // Расписание для группы

                    for (int z = 0; z < jShedule.length(); z++){ // заполнение расписания подгруппы
                        String[] corpAud = jShedule.getJSONObject(z).getString("subject2").split("-");

                        audsSchedule.clear();
                        if (jShedule.getJSONObject(z).isNull("subject2"))
                            audsSchedule.add(new Auds("Неизвестно","Неизвестно"));
                        else
                            audsSchedule = audsService.findByAud(corpAud[0], jShedule.getJSONObject(z).getString("subject2")); // поиск аудитории

                        teacherSchedule.clear();
                        if (jShedule.getJSONObject(z).isNull("subject3"))
                            teacherSchedule.add(new Teachers("Неизвестно"));
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
                }
                System.out.println("Save for: " + jDirections.getJSONObject(j).getString("title"));
            }
        }
        /*allLessons = lessonService.findAllLessons();
        for (Lesson l: allLessons) {// заполнение смежной таблицы (подгруппа - занятие)
            groupSchedule = studentsService.findBySubgroup(l.getSubgroup());
            LessonBySubgroup lessonBySubgroup = new LessonBySubgroup();
            lessonBySubgroup.setStudent(groupSchedule.get(0));
            lessonBySubgroup.setLesson(l);
            lessonBySubgroupService.saveLesson(lessonBySubgroup);
        }*/
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
