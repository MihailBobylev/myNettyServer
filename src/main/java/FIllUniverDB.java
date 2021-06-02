import models.*;
import org.json.JSONArray;
import services.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class FIllUniverDB {
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
        studentsService.deleteAllStudents();
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
        lessonBySubgroupService.deleteAllLessonsBySubgroup();
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
    public String getUrl(){

        return "https://ksu.edu.ru/timetable/" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH)/8 + 1) + "/timetable.php";

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
