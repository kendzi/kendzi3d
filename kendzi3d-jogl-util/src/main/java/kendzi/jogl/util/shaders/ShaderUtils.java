package kendzi.jogl.util.shaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import kendzi.jogl.glu.GLException;
import org.lwjgl.opengl.GL20C;

public class ShaderUtils {
    static final ConcurrentMap<String, Shader> SHADER_MAP = new ConcurrentHashMap<>();

    private ShaderUtils() {
        // Hide constructor
    }

    /**
     * Get a shader
     * 
     * @param filename
     *            The file shader
     * @param type
     *            See {@link GL20C#glCreateShader(int)}
     * @return The shader to use to bind/unbind
     */
    static Shader getShader(final String filename, final int type) {
        final Path path = Paths.get(filename);
        if (Files.isReadable(path)) {
            Shader shader = new Shader(GL20C.glCreateShader(type));
            try {
                shader.load(Files.readAllBytes(path));
            } catch (IOException e) {
                throw new GLException(e);
            }
            return shader;
        } else if (ShaderUtils.class.getClassLoader().getResource(filename) != null) {
            // Not a file path. Try reading from classpath.
            try (InputStream inputStream = ShaderUtils.class.getClassLoader().getResourceAsStream(filename);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                // TODO: With Java 9, we can use InputStream#readAllBytes
                int readByteNumber;
                byte[] readBytes = new byte[32];
                while ((readByteNumber = inputStream.read(readBytes)) != -1) {
                    outputStream.write(readBytes, 0, readByteNumber);
                }
                // Flush just in case the ByteArrayOutputStream implementation does something
                // funky.
                outputStream.flush();
                Shader shader = new Shader(GL20C.glCreateShader(type));
                shader.load(outputStream.toByteArray());
                return shader;
            } catch (IOException e) {
                throw new GLException(e);
            }
        }
        throw new GLException("Could not find shader file for " + filename);
    }

    /**
     * Get a shader program
     * 
     * @param shaderTypeMap
     *            A map of shaders to shader types (see
     *            {@link GL20C#glCreateShader(int)})
     * @return The new shader program
     */
    public static ShaderProgram getShaderProgram(final Map<String, Integer> shaderTypeMap) {
        return new ShaderProgram(shaderTypeMap.entrySet().stream().map(entry -> getShader(entry.getKey(), entry.getValue()))
                .toArray(Shader[]::new));
    }
}
