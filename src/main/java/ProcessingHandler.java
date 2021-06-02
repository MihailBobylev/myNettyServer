import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;
import java.util.*;

public class ProcessingHandler extends ChannelInboundHandlerAdapter {
    public static final String PATH_TO_PROPERTIES = "src/main/resources/serv.properties";
    static FileInputStream fileInputStream;
    static Properties prop = new Properties();

    FIllUniverDB fIllUniverDB = new FIllUniverDB();
    GetTimetable getTimetable = new GetTimetable();
    FillFloors fillFloors = new FillFloors();

    static {
        try {
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            prop.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Клиент подключился");
        RequestData requestData = (RequestData) msg;
        // case по параметру (0, 1, 2, 3, 4, 5), опредлеляющему запрос пользователя
        ResponseData responseData = new ResponseData();

        if(!prop.getProperty("url").equals(fIllUniverDB.getUrl()))
        {
            prop.setProperty("url", fIllUniverDB.getUrl());
            prop.store(new FileOutputStream("src/main/resources/serv.properties"), null);
            fIllUniverDB.FillTeachers();
            fIllUniverDB.FillAuds();
            fIllUniverDB.FillStudents();
            fIllUniverDB.FillLessons();
            fIllUniverDB.FillLessonBySubgroup();
        }
        switch (Integer.parseInt(requestData.getFlag())){
            case 0: // расписание аудитории
                responseData = getTimetable.getAudSchedule(requestData);

                System.out.println("Преподаватель: " + responseData.getTeacherName());
                System.out.println("Дисциплина: " + responseData.getLessonName());
                System.out.println("Тип дисциплины: " + responseData.getLessonType());
                System.out.println("Группа: " + responseData.getGroupName());
                break;
            case 1: // расписание препода
                responseData = getTimetable.getTeacherSchedule(requestData);

                System.out.println("Преподаватель: " + requestData.getTeacherName());
                System.out.println("Корпус: " + responseData.getCorps());
                System.out.println("Аудитория: " + responseData.getAuditor());
                System.out.println("Номер пары: " + responseData.getLessonNumber());
                System.out.println("Дисциплина: " + responseData.getLessonName());
                System.out.println("Тип дисциплины: " + responseData.getLessonType());
                System.out.println("Группа: " + responseData.getGroupName());
                break;
            case 2: // расписание группы
                responseData = getTimetable.getStudentsSchedule(requestData);

                System.out.println("Номер пары: " + responseData.getLessonNumber());
                System.out.println("Корпус: " + responseData.getCorps());
                System.out.println("Аудитория: " + responseData.getAuditor());
                System.out.println("Дисциплина: " + responseData.getLessonName());
                System.out.println("Тип дисциплины: " + responseData.getLessonType());
                System.out.println("Преподаватель: " + responseData.getTeacherName());
                break;
            case 3: // поиск пути
                List<String> route = getTimetable.buildRoute(requestData.getAuditor().substring(2),requestData.getEndAuditor().substring(2));
                if(route.size() == 0)
                    responseData.setWays("Неизвестно");

                for (String s:route) {
                    System.out.println(s);
                }

                String way = "";
                for (String s: route) {
                    way += s + "@";
                }
                responseData.setWays(way);
                break;
            case 4: // заполнение БД клиента
                responseData = getTimetable.fillClientDB();

                System.out.println("Институт: " + responseData.getInstitute());
                System.out.println("Направление: " + responseData.getDirection());
                System.out.println("Группа: " + responseData.getGroupName());

                System.out.println("Преподаватель: " + responseData.getTeacherName());

                System.out.println("Корпус: " + responseData.getCorps());
                System.out.println("Аудитория: " + responseData.getAuditor());
                break;
            case 5:
                fillFloors.FillFirstFloor();
                fillFloors.FillSecondFloor();
                fillFloors.FillThirdFloor();
                fillFloors.FillFourthFloor();
                fillFloors.FillFifthFloor();
                break;
        }
        ChannelFuture future = ctx.writeAndFlush(responseData);
        future.addListener(ChannelFutureListener.CLOSE);
    }
}
