package io.miso.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DataOutputHandler extends ByteArrayOutputStream {
    private final DataOutputStream dataOutputStream;

    public DataOutputHandler() {
        dataOutputStream = new DataOutputStream(this);
    }

    public void writeUnsignedInt(final int i) throws IOException {
        dataOutputStream.writeInt(i);
        flush();
    }

    public void writeUnsignedShort(final short s) throws IOException {
        dataOutputStream.writeShort(s & 0xFFFF);
        flush();
    }

    public void writeUnsignedByte(final byte b) throws IOException {
        dataOutputStream.writeByte(b & 0xFF);
        flush();
    }

    public void writeUnsignedLong(final long l) throws IOException {
        dataOutputStream.writeLong(l);
        flush();
    }

}
