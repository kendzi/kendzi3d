package kendzi.jogl.util.shaders;

import kendzi.jogl.glu.GLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20C;

public class ShaderProgram implements AutoCloseable {
    private final int id;

    ShaderProgram(Shader... shaders) {
        this.id = GL20C.glCreateProgram();
        for (Shader shader : shaders) {
            GL20C.glAttachShader(this.id, shader.getId());
        }
        GL20C.glLinkProgram(this.id);
        if (GL11.GL_FALSE == GL20C.glGetProgrami(this.id, GL20C.GL_LINK_STATUS)) {
            throw new GLException("Shader Program Linking failed:\n" + GL20C.glGetProgramInfoLog(this.id));
        }
        for (Shader shader : shaders) {
            shader.close();
        }
    }

    /**
     * Start using the shader program
     */
    public void use() {
        GL20C.glUseProgram(this.id);
    }

    /**
     * Stop using the shader program
     */
    public void stopUsing() {
        // Reset the program to default (undefined behavior though)
        GL20C.glUseProgram(0);
    }

    /**
     * See {@link GL20C#glGetUniformLocation(int, CharSequence)}
     * 
     * @param argument
     *            a null terminated string containing the name of the uniform
     *            variable whose location is to be queried
     * @return the location of a uniform variable.
     */
    public int getUniformLocation(CharSequence argument) {
        return GL20C.glGetUniformLocation(this.id, argument);
    }

    @Override
    public void close() throws GLException {
        if (!GL20C.glIsProgram(this.id)) {
            throw new GLException("Shader attempted to be closed twice");
        }
        GL20C.glDeleteProgram(this.id);
    }
}
