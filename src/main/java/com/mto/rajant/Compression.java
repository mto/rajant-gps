package com.mto.rajant;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date: 2/20/17
 */
public enum Compression {

    NONE(0), GZIP(2), LZMA(4), LZ(8);

    private int _bit;

    private Compression(int bit) {
        _bit = bit;
    }

    public int encode() {
        return _bit;
    }

    public static Compression decode(int bit) {
        switch (bit) {
            case 0:
                return NONE;
            case 2:
                return GZIP;
            case 4:
                return LZMA;
            case 8:
                return LZ;
            default:
                throw new IllegalArgumentException("bit");
        }
    }
}
