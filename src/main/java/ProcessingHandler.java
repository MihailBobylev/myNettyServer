import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

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
    Connection c;
    Statement stmt;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestData requestData = (RequestData) msg;
        String schedule = "";
        //ResultSet rs;
        //ResponseData responseData = new ResponseData();
        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/univer","postgres", "root");
            c.setAutoCommit(false);
            System.out.println("-- Opened database successfully");

            String sql = "SELECT id, number, schedule FROM corp_e WHERE number = " + "'" + requestData.getAuditor() + "'" + ";";
            //--------------- SELECT DATA ------------------
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String s2 = rs.getString("number");
                schedule  = rs.getString("schedule");
                System.out.println(id + " " + s2 + " " + schedule);
            }
            rs.close();
            stmt.close();
            c.commit();
            System.out.println("-- Operation SELECT done successfully");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        ResponseData responseData = new ResponseData();
        if(schedule == "") {
            responseData = getCurrentClass(requestData.getAuditor(), 1, 2, 0);
            System.out.println(responseData.getClassName());
            System.out.println(responseData.getGroupName());
            System.out.println(responseData.getTeacherName());
        }
        else {

        }
        //responseData.setIntValue(requestData.getIntValue() * 2);
        ChannelFuture future = ctx.writeAndFlush(responseData); //responseData
        future.addListener(ChannelFutureListener.CLOSE);
        System.out.println(requestData.getStringValue());
    }
    public String getUrl(){

        return "https://ksu.edu.ru/timetable/" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH)/8 + 1) + "/timetable.php";

    }
    public ResponseData getCurrentClass(String room, int dayOfWeek, int lessonNumber, int week) throws IOException, ClassNotFoundException, SQLException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action","getauds");
        params.put("id", "5");// корпус Е

        String roomsJSON = doPostQuery(getUrl(), params); //"https://ksu.edu.ru/timetable/2020_1/timetable.php"
        JSONArray jObj = new JSONArray(roomsJSON);
        String post_id = null;
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
        Class.forName("org.postgresql.Driver");
        c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/univer","postgres", "root");
        c.setAutoCommit(false);
        stmt = c.createStatement();
        String sql2 = "INSERT INTO corp_e (number, schedule) VALUES (325, " + "'" + jObj + "'" + ");";
        stmt.executeUpdate(sql2);

        stmt.close();
        c.commit();
        System.out.println("-- Records created successfully");
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
