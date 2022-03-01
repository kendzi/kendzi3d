package kendzi.jogl.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

@Deprecated
public class Buffers {
    public static int sizeOfBufferElem(final Buffer buffer) {
        if (buffer instanceof ByteBuffer) {
            return Byte.BYTES;
        } else if (buffer instanceof IntBuffer) {
            return Integer.BYTES;
        } else if (buffer instanceof ShortBuffer) {
            return Short.BYTES;
        } else if (buffer instanceof FloatBuffer) {
            return Float.BYTES;
        } else if (buffer instanceof DoubleBuffer) {
            return Double.BYTES;
        } else if (buffer instanceof LongBuffer) {
            return Long.BYTES;
        } else if (buffer instanceof CharBuffer) {
            return Character.BYTES;
        } else {
            throw new IllegalArgumentException("Unknown buffer type: " + buffer.getClass());
        }
    }
}
