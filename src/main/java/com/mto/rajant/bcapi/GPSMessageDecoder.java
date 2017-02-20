package com.mto.rajant.bcapi;

import com.rajant.bcapi.protos.GpsProtos;
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
public class GPSMessageDecoder extends MessageToMessageDecoder<WireMessage> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, WireMessage wireMessage, List<Object> list) throws Exception {
        InputStream in = decompress(wireMessage, new ByteBufInputStream(wireMessage.payload));
        list.add(GpsProtos.GPS.parseFrom(in));
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
