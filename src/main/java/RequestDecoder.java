import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class RequestDecoder extends ReplayingDecoder<RequestData> {

    // получение данных от клиента
    private final Charset charset = Charset.forName("Cp866");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        RequestData data = new RequestData();

        int flagLen = in.readInt();
        data.setFlag(in.readCharSequence(flagLen, charset).toString());// получаем флаг

        int audLen = in.readInt();
        data.setAuditor(in.readCharSequence(audLen, charset).toString());// получаем аудиторию

        int corpLen = in.readInt();
        data.setCorp(in.readCharSequence(corpLen, charset).toString());// получаем корпус

        int dayLen = in.readInt();
        data.setDayOfWeek(in.readCharSequence(dayLen, charset).toString());// получаем день недели

        int lessonLen = in.readInt();
        data.setLessonNumber(in.readCharSequence(lessonLen, charset).toString());// получаем занятие

        int weekLen = in.readInt();
        data.setWeek(in.readCharSequence(weekLen, charset).toString());// получаем четность недели

        int instituteLen = in.readInt();
        data.setInstitute(in.readCharSequence(instituteLen, charset).toString());// получаем институт

        int directionLen = in.readInt();
        data.setDirection(in.readCharSequence(directionLen, charset).toString());// получаем направление обучения

        int groupLen = in.readInt();
        data.setGroup(in.readCharSequence(groupLen, charset).toString());// получаем группу

        int teachLen = in.readInt();
        data.setTeacherName(in.readCharSequence(teachLen, charset).toString());// получаем препода

        int audLen2 = in.readInt();
        data.setEndAuditor(in.readCharSequence(audLen2, charset).toString());// получаем конечную аудиторию

        out.add(data);
    }
}
