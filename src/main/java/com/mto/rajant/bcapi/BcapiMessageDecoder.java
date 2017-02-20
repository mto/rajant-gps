package com.mto.rajant.bcapi;

import com.rajant.bcapi.protos.BCAPIProtos;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date: 2/20/17
 */
public class BcapiMessageDecoder extends MessageToMessageDecoder<WireMessage> {

    @Override
    protected void decode(ChannelHandlerContext ctx, WireMessage wire, List<Object> out) throws Exception {
        InputStream in = decompress(wire, new ByteBufInputStream(wire.payload));
        out.add(BCAPIProtos.BCMessage.parseFrom(in));
    }
    
    private InputStream decompress(WireMessage msg, InputStream in) throws IOException {
        switch (msg.compression) {
            case NONE:
                return in;
            case GZIP:
                return new InflaterInputStream(in, new Inflater(true));
            default:
                throw new IllegalStateException("compression mode not supported: " + msg.compression);
        }
    }
}
