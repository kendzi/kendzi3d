package kendzi.jogl.util;

import java.util.function.IntConsumer;

import org.lwjgl.opengl.GL15C;

public class BufferObject implements AutoCloseable {
    private final int type;
    private final int id;
    private final int count;

    /**
     * Create a new BufferObject
     * 
     * @param type
     *            The type (see {@link GL15C#glBindBuffer(int, int)} for valid
     *            types)
     */
    public BufferObject(int type, int count, IntConsumer generateBufferObject) {
        this.type = type;
        this.id = GL15C.glGenBuffers();
        this.count = count;
        this.bindBuffer();
        generateBufferObject.accept(type);
        this.unbindBuffer();
    }

    public void bindBuffer() {
        GL15C.glBindBuffer(this.type, this.id);
    }

    public void unbindBuffer() {
        GL15C.glBindBuffer(this.type, 0);
    }

    @Override
    public void close() {
        GL15C.glDeleteBuffers(this.id);
    }

    public int getId() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }

    public int count() {
        return this.count;
    }
}
