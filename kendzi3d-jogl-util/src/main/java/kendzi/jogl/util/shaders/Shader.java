package kendzi.jogl.util.shaders;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import kendzi.jogl.glu.GLException;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL20C;

public class Shader implements AutoCloseable {

    private final int shaderId;

    Shader(final int shaderId) {
        this.shaderId = shaderId;
    }

    void load(final byte[] bytes) {
        final CharSequence charSequence = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes));
        GL20C.glShaderSource(this.shaderId, charSequence);
        GL20C.glCompileShader(this.shaderId);
        if (GL20C.glGetShaderi(this.shaderId, GL20C.GL_COMPILE_STATUS) == GL11C.GL_FALSE) {
            throw new GLException("Could not compile shader:\n" + GL20C.glGetShaderInfoLog(this.shaderId));
        }
    }

    @Override
    public void close() throws GLException {
        if (!GL20C.glIsShader(this.shaderId)) {
            throw new GLException("Attempting to close already closed shader");
        }
        GL20C.glDeleteShader(this.shaderId);
    }

    /**
     * The shader id
     * 
     * @return The shader id
     */
    public int getId() {
        return this.shaderId;
    }
}
