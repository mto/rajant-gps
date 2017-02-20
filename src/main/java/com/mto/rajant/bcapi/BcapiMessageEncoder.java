package com.mto.rajant.bcapi;

import com.mto.rajant.Compression;
import com.rajant.bcapi.protos.BCAPIProtos;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.OutputStream;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date: 2/20/17
 */
public class BcapiMessageEncoder extends MessageToMessageEncoder<BCAPIProtos.BCMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, BCAPIProtos.BCMessage msg, List<Object> out) throws Exception {
        WireMessage wire = new WireMessage();
        wire.payload = ctx.alloc().buffer();
        try (OutputStream os = new DeflaterOutputStream(new ByteBufOutputStream(wire.payload), new Deflater(9, true))) {
            msg.writeTo(os);
        }
        wire.length = wire.payload.writerIndex();
        wire.compression = Compression.GZIP;
        out.add(wire);
    }
}
