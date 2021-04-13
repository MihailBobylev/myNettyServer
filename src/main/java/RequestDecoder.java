import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class RequestDecoder extends ReplayingDecoder<RequestData> {

    // получение данных от клиента
    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        RequestData data = new RequestData();

        int audLen = in.readInt();
        data.setAuditor(in.readCharSequence(audLen, charset).toString());// получаем аудиторию от клиента

        int strLen = in.readInt();
        data.setStringValue(in.readCharSequence(strLen, charset).toString());// получаем строку от клиента
        out.add(data);
    }
}
