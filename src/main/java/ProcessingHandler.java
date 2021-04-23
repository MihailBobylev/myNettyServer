import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import services.AudsService;
import services.StudentsService;
import services.TeachersService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProcessingHandler extends ChannelInboundHandlerAdapter {
    private Calendar calendar = Calendar.getInstance();
    AudsService audsService = new AudsService();
    StudentsService studentsService = new StudentsService();
    TeachersService teachersService = new TeachersService();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestData requestData = (RequestData) msg;
        String schedule = "";
        //ResultSet rs;
        //ResponseData responseData = new ResponseData();
        // Поиск данных в БД
        // case по параметру (0, 1, 2, 3), опредлеляющему запрос пользователя
        int flag = 1;
        switch (flag){
            case 0: // рапсисание аудитории
                break;
            case 1: // расписание препода
                break;
            case 2: // расписание группы
                break;
            case 3: // поиск пути
                break;

        }
        //-----------------------
        ResponseData responseData = new ResponseData();
        if(schedule == "") {
            //Расписание группы

            //Расписание препода
            /*responseData = getTeacher("Бабенко А.С.");
            System.out.println(responseData.getTeacherName());
            System.out.println(responseData.getSchedule());*/

            //расписание аудитории
            /*responseData = getCurrentClass(requestData.getAuditor(), 1, 2, 0);
            System.out.println(responseData.getClassName());
            System.out.println(responseData.getGroupName());
            System.out.println(responseData.getTeacherName());*/
        }
        else {

        }
        //responseData.setIntValue(requestData.getIntValue() * 2);
        ChannelFuture future = ctx.writeAndFlush(responseData); //responseData
        future.addListener(ChannelFutureListener.CLOSE);
        System.out.println(requestData.getName());
    }
    public String getUrl(){

        return "https://ksu.edu.ru/timetable/" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH)/8 + 1) + "/timetable.php";

    }
    public ResponseData getCurrentClass(String build, String room, int dayOfWeek, int lessonNumber, int week) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "getbuildings");

        String buildingsJSON = doPostQuery(getUrl(), params); // список всех корпусов
        JSONArray jObj = new JSONArray(buildingsJSON);
        String post_id = null;
        for (int i = 0; i < jObj.length(); i++){
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

        //---------------------------------------------

        String x = String.valueOf(dayOfWeek);
        String y = String.valueOf(lessonNumber);
        String n = String.valueOf(week);
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

    public ResponseData getTeacher(String name) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "getteachers");

        String teachersJSON = doPostQuery(getUrl(), params); // список всех преподов
        JSONArray jObj = new JSONArray(teachersJSON);
        String post_id = null;
        for (int i = 0; i < jObj.length(); i++){
            if (jObj.getJSONObject(i).getString("title").equals(name)) {
                post_id = jObj.getJSONObject(i).getString("id");
                break;
            }
        }
        if (post_id == null)
            return null;
        //--------------------------------

        params = new HashMap<String, String>();
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

            //---------------------------------------------

            ResponseData l = new ResponseData();
            l.setTeacherName(name);
            l.setSchedule(jObj.toString());
            return l;
        }
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

        facultiesJSON = doPostQuery(getUrl(), params); // Все группы
        jObj = new JSONArray(facultiesJSON);

        if (jObj.length() == 0)
            return null;
        else
        {
            // Записываем расписание для препода в БД

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
