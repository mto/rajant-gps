package com.mto.rajant.bcapi;

import com.mto.rajant.Compression;
import io.netty.buffer.ByteBuf;

public class WireMessage {

    public int length;
    public Compression compression;
    public ByteBuf payload;
}
