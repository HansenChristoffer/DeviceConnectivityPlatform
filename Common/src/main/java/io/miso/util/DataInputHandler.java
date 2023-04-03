package io.miso.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class DataInputHandler extends ByteArrayInputStream {
    private final DataInputStream dos;

    public DataInputHandler(final byte[] buf) {
        super(buf);
        dos = new DataInputStream(this);
    }

    public long readLong() throws IOException {
        return dos.readLong();
    }

    public int readUnsignedByte() throws IOException {
        return dos.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
        return dos.readUnsignedShort();
    }

    public long readUnsignedInt() throws IOException {
        return dos.readInt() & 0xFFFF_FFFF;
    }

    public boolean readBoolean() throws IOException {
        return dos.readBoolean();
    }

    public byte[] readBytes() throws IOException {
        return dos.readAllBytes();
    }
}
