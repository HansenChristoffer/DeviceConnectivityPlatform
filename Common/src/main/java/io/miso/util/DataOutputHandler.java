package io.miso.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DataOutputHandler extends ByteArrayOutputStream {
    private final DataOutputStream dataOutputStream;

    public DataOutputHandler() {
        dataOutputStream = new DataOutputStream(this);
    }

    public void writeLong(final long l) throws IOException {
        dataOutputStream.writeLong(l);
        flush();
    }

    public void writeUnsignedInt(final long i) throws IOException {
        dataOutputStream.writeInt((int) i);
        flush();
    }

    public void writeUnsignedShort(final int s) throws IOException {
        dataOutputStream.writeShort((short) s & 0xFFFF);
        flush();
    }

    public void writeUnsignedByte(final short b) throws IOException {
        dataOutputStream.writeByte((byte) b & 0xFF);
        flush();
    }

    public void writeUnsignedLong(final long l) throws IOException {
        dataOutputStream.writeLong(l);
        flush();
    }
}
