package com.mto.rajant.bcapi;

import com.mto.rajant.Compression;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date: 2/20/17
 */
public class BcapiWireDecoder extends ByteToMessageDecoder {

    private static enum State { HEADER, BODY }

    private State _state = State.HEADER;

    private WireMessage _wire;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (_state) {
            case HEADER:
                // each message starts with an uncompressed binary header
                if (in.readableBytes() < 8) return;
                _wire = new WireMessage();
                // payload length
                _wire.length = in.readInt();
                // compression mode
                _wire.compression = Compression.decode(in.readByte());
                // reserved bytes... discard
                in.readBytes(3);
                _state = State.BODY;
                break;
            case BODY:
                // after the header is a byte array corresponding to a serialized
                // instance of BCMessage
                if (in.readableBytes() < _wire.length) return;
                _wire.payload = in.readBytes(_wire.length);
                out.add(_wire);
                _state = State.HEADER;
                break;
        }
    }
}
