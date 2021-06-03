import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class ResponseDataEncoder extends MessageToByteEncoder<ResponseData> {
    // отправка данных клиенту
    private final Charset charset = Charset.forName("Cp866");
    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseData msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getLessonName().length()); //.getBytes().
        out.writeCharSequence(msg.getLessonName(), charset);

        out.writeInt(msg.getGroupName().length());
        out.writeCharSequence(msg.getGroupName(), charset);

        out.writeInt(msg.getTeacherName().length());
        out.writeCharSequence(msg.getTeacherName(), charset);

        out.writeInt(msg.getCorps().length());
        out.writeCharSequence(msg.getCorps(), charset);

        out.writeInt(msg.getAuditor().length());
        out.writeCharSequence(msg.getAuditor(), charset);

        out.writeInt(msg.getLessonNumber().length());
        out.writeCharSequence(msg.getLessonNumber(), charset);

        out.writeInt(msg.getLessonType().length());
        out.writeCharSequence(msg.getLessonType(), charset);

        out.writeInt(msg.getWays().length());
        out.writeCharSequence(msg.getWays(), charset);

        out.writeInt(msg.getInstitute().length());
        out.writeCharSequence(msg.getInstitute(), charset);

        out.writeInt(msg.getDirection().length());
        out.writeCharSequence(msg.getDirection(), charset);

        out.writeInt(msg.getNumberOfClass().length());
        out.writeCharSequence(msg.getNumberOfClass(), charset);

        out.writeInt(msg.getNumberOfDay().length());
        out.writeCharSequence(msg.getNumberOfDay(), charset);

        out.writeInt(msg.getNumberOfWeek().length());
        out.writeCharSequence(msg.getNumberOfWeek(), charset);

        out.writeInt(msg.getSubgroup().length());
        out.writeCharSequence(msg.getSubgroup(), charset);
    }
}
