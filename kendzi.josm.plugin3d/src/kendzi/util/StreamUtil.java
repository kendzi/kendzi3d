package kendzi.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class StreamUtil {

    private static final int BUFFER_SIZE = 4096;

    /**
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {

        if (in == null) {
            throw new IllegalArgumentException("error: no inputStream specified");
        }

        if (out == null) {
            throw new IllegalArgumentException("error: no outputStream specified");
        }

        try {

            int byteCount = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }

            out.flush();

            return byteCount;
        }
        finally {

            try {
                in.close();
            }
            catch (IOException ex) {
                //
            }

            try {
                out.close();
            }
            catch (IOException ex) {
                //
            }
        }
    }
}
