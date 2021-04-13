import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class ResponseDataEncoder extends MessageToByteEncoder<ResponseData> {
    // отправка данных клиенту
    private final Charset charset = Charset.forName("UTF-8");
    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseData msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getClassName().getBytes().length);
        out.writeCharSequence(msg.getClassName(), charset);

        out.writeInt(msg.getGroupName().getBytes().length);
        out.writeCharSequence(msg.getGroupName(), charset);

        out.writeInt(msg.getTeacherName().getBytes().length);
        out.writeCharSequence(msg.getTeacherName(), charset);

    }
}
