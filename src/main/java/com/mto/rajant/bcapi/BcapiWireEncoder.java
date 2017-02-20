package com.mto.rajant.bcapi;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date: 2/20/17
 */
public class BcapiWireEncoder extends MessageToByteEncoder<WireMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, WireMessage wire, ByteBuf out) throws Exception {
        out.writeInt(wire.length);
        out.writeByte(wire.compression.encode());
        out.writeZero(3);
        out.writeBytes(wire.payload);
    }
}
