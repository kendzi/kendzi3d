package kendzi.josm.kendzi3d.jogl.model.export;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import kendzi.josm.kendzi3d.jogl.model.export.ui.ExportOutput;
import kendzi.josm.kendzi3d.service.TextureCacheService;
import kendzi.kendzi3d.collada.ColladaExport;
import kendzi.kendzi3d.collada.TextExport;

import org.apache.log4j.Logger;

public class ExportWorker extends Thread {

    /** Log. */
    private static final Logger log = Logger.getLogger(ExportWorker.class);

    private List<ExportItem> items;

    private ExportModelConf conf;

    private ExportOutput logFrame;

    /**
     * Texture cache service.
     */
    private TextureCacheService textureCacheService;

    /**
     * @param items
     * @param conf
     */
    public ExportWorker(List<ExportItem> items, ExportModelConf conf, TextureCacheService textureCacheService) {
        this.items = items;
        this.conf = conf;
        this.textureCacheService = textureCacheService;

        setName("Model Export Thread");
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        createLogUi();

        TextExport exporter = new ColladaExport();
        // if (conf.type == "obj") {
        //     saveToObjFile(ei);
        // }

        File file = getFile(this.conf.getFilePattern(), 0, this.conf.getExportType());

        int c = 0;
        int count =  this.items.size();

        for(ExportItem ei : this.items) {
            try {
                c++;
                addToLog("Export model: " + c + " of " + count);
                exporter.addModel(ei.getModel());
            } catch (Exception e) {
                log.error("Error exporting model: " + ei, e);
                addToLog(e.getMessage());
            }
        }
        c = 0;
        File parent = file.getParentFile();
        for (String textureKey : exporter.getTextureKeys().keySet()) {
            try {

                c++;
                addToLog("Export texture: " + c);

                log.info(textureKey);

                BufferedImage image = this.textureCacheService.getImage(textureKey);
                if (image != null) {
                    File textureFile = new File(parent, exporter.getTextureKeys().get(textureKey));
                    textureFile.getParentFile().mkdirs();

                    String format = getFormat(textureFile);
                    ImageIO.write(image, format, textureFile);
                } else {
                    log.error("cant load image for key: " + textureKey);
                    addToLog("cant load image for key: " + textureKey);
                }
//            textureFile.get
            } catch (Exception e) {
                log.error(e);
                addToLog(e.getMessage());
            }
        }

        try {
            addToLog("starting save");
            exporter.save(file.getAbsolutePath());
            addToLog("end save");
        } catch (Throwable e) {
            log.error("Error saving file: " + file, e);
            addToLog(e.getMessage());
        }
    }

    private File getFile(String filePattern, int i, Object type) {

        String extension = null;
        if ("collada".equals(type)) {
            extension = ".dae";
        } else {
            throw new RuntimeException("unsuported type: " + type);
        }

        if (filePattern == null) {
            throw new RuntimeException("pattern can't be null");
        }


        if (filePattern.toUpperCase().endsWith(extension.toUpperCase())) {
            filePattern = filePattern.substring(0, filePattern.length() - extension.length());
        }

        filePattern = filePattern + "." + i + extension;

        //filePattern = filePattern.replaceAll("\\.dxf^", "." + i + ".dxf");

        return new File(filePattern);
    }

    private String getFormat(File file) {
        if (file == null) {
            return null;
        }
        String str = file.getAbsolutePath();
        int i = str.lastIndexOf('.');
        if (i < 0) {
            return "png";
        }
        String ext = str.substring(str.lastIndexOf('.')+1, str.length()).toLowerCase();
        if ("png".equals(ext)) {
            return ext;
        } else if ("jpg".equals(ext)) {
            return ext;
        }
        return "png";
    }

    private void createLogUi() {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    logFrame = new ExportOutput();
                    ExportWorker.this.logFrame.setVisible(true);
                } catch (Exception e) {
                    log.error(e);
                }
            }
        });
    }

    private void addToLog(final String str) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ExportWorker.this.logFrame.addLog(str);
                } catch (Exception e) {
                    log.error(e);
                }
            }
        });
    }
}
