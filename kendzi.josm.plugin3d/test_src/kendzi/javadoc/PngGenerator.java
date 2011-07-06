/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.javadoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts svg to png from javadocs.
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public class PngGenerator {
//    private static String filesDir = "C:/dane_tomekk/eclipse/workspace2/kendzi.josm.kendzi3d/src/kendzi/math/geometry";
    private static String filesDir = "C:/dane_tomekk/eclipse/workspace2/kendzi.josm.plugin3d/src/kendzi/josm/kendzi3d/jogl/model/roof/mk/type";
    private static String cmd = "c:/Program Files/Inkscape/inkscape.exe \"{0}\" --export-png \"{1}\" ";

    public static void main(String[] args) throws IOException {

        System.out.println((new File(filesDir)).toURL());

        List<File> files =findSubFiles(new File(filesDir));

        String s = null;

        for (File f : files) {

            String svg = f.getAbsolutePath();
            String png = svg.substring(0,svg.length()-3) + "png";

            String runCmd = cmd.replace("{0}", svg);
            runCmd = runCmd.replace("{1}", png);
            System.out.println(svg);
            System.out.println(png);
            System.out.println(runCmd);

            Process p =  Runtime.getRuntime().exec(runCmd);

            writeProcesOutput(p);


        }
    }

    private static void writeProcesOutput(Process p) throws IOException {
        String s;
        BufferedReader stdInput = new BufferedReader(new
             InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
             InputStreamReader(p.getErrorStream()));

        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        // read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }

    static List<File> findSubFiles(File dir) {

        List<File> ret = new ArrayList<File>();

//        FilenameFilter filter = new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                return !name.endsWith(".svg");
//            };
//        };

        File[] files = dir.listFiles();
        for (int x = 0; x < files.length; ++x) {
            if (files[x].isDirectory()) {
                ret.addAll(findSubFiles(files[x]));
            } else if (isAccept(files[x].getName())) {
                ret.add(files[x]);
            }
        }
        return ret;


    }
    public static boolean isAccept(String name) {
        return name.endsWith(".svg");
    };



}
