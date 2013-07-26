package kendzi.kendzi3d.collada;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;

public abstract class TextExport {

    public abstract void addModel(kendzi.jogl.model.geometry.Model model) throws Exception;

    public abstract void save(String fileName) throws Throwable;

    public abstract Map<String, String> getTextureKeys();

    protected static void saveFile(String fileName, String str) throws FileNotFoundException {
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(fileName));
            out.print(str);
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }



}
