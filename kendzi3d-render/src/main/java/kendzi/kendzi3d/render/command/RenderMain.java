package kendzi.kendzi3d.render.command;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import kendzi.josm.datasource.Bbox;
import kendzi.kendzi3d.render.ImageRender;
import kendzi.kendzi3d.render.RenderResult;
import kendzi.kendzi3d.render.conf.RenderDataSourceConf;
import kendzi.kendzi3d.render.conf.RenderEngineConf;
import kendzi.kendzi3d.render.dataset.DataSetProvider;
import kendzi.kendzi3d.render.dataset.DataSetProviderFactory;
import kendzi.kendzi3d.render.module.RenderModule;
import kendzi.kendzi3d.render.tile.Tile;
import kendzi.util.StreamUtil;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.coor.LatLon;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;

public class RenderMain {
    /** Log. */
    private static final Logger log = Logger.getLogger(RenderMain.class);


    public static void main(String[] args) {
        try {
            CliRenderOptions result = CliFactory.parseArguments(CliRenderOptions.class, args);

            if (result.getHelp()) {
                printHelp();
                System.exit(0);
            }

            validate(result);

            RenderEngineConf reConf = convertRenderEngineConf(result);
            RenderDataSourceConf dsConf = convertRenderDataSourceConf(result);

            DataSetProvider dataSet = DataSetProviderFactory.loadConf(dsConf);

            Injector injector = Guice.createInjector(new RenderModule(reConf, dataSet));


            ImageRender ir = injector.getInstance(ImageRender.class);

            ir.init();

            RenderResult render = null;

            if (result.isOviewTile()) {
                render = ir.render(parseTile(result.getOviewTile()));
            } else if (result.isOviewBbox()) {
                Bbox bbox = parseBbox(result.getOviewBbox());

                LatLon leftTop = new LatLon(bbox.getLat_max(), bbox.getLon_min());
                LatLon rightBottom = new LatLon(bbox.getLat_min(), bbox.getLon_max());

                render = ir.render(leftTop, rightBottom);
            }

            ir.release();

            save(render.getImage(), result.getOutput());


        } catch (ArgumentValidationException e) {
            log.error("Error parsing input arguments",e);
        }
    }


    private static void save(byte[] image, String fileName) {

        if (image == null) {
            throw new RuntimeException("tileBytes cant be null!");
        }

        ByteArrayInputStream in = new ByteArrayInputStream(image);

        File f = new File(fileName);

        try {
            File parent = f.getParentFile();
            parent.mkdirs();

            FileOutputStream out = new FileOutputStream(f);

            StreamUtil.copy(in, out);

            log.info("saved output to file: " + fileName);

        } catch (IOException e) {
            log.error("Error saving file", e);
        }
    }

    private static Bbox parseBbox(String oviewBbox) {
        try {
            String[] s = oviewBbox.split(",");
            double lat1 = Double.parseDouble(s[0]);
            double lon1 = Double.parseDouble(s[1]);
            double lat2 = Double.parseDouble(s[2]);
            double lon2 = Double.parseDouble(s[3]);
            Bbox bbox = new Bbox(lon1, lat1);
            bbox.addPoint(lon2, lat2);

            return bbox;

        } catch (Exception e) {
            throw new IllegalArgumentException("bad oview.bbox format: " + oviewBbox, e);
        }
    }


    private static Tile parseTile(String oviewTile) {
        try {
            String[] s = oviewTile.split(",");
            int z = Integer.parseInt(s[0]);
            int x = Integer.parseInt(s[1]);
            int y = Integer.parseInt(s[2]);

            return new Tile(x, y, z);

        } catch (Exception e) {
            throw new IllegalArgumentException("bad oview.tile format: " + oviewTile, e);
        }
    }


    private static RenderDataSourceConf convertRenderDataSourceConf(
            CliRenderOptions r) {

        RenderDataSourceConf c = new RenderDataSourceConf();

        c.setInputSource(r.getInputSource());
        c.setFileUrl(r.getFileUrl());
        c.setJdbcUrl(r.getDbUrl());
        c.setJdbcUsername(r.getDbUsername());
        c.setJdbcPassword(r.getDbPassword());

        return c;
    }


    private static RenderEngineConf convertRenderEngineConf(
            CliRenderOptions r) {
        RenderEngineConf c = new RenderEngineConf();
        c.setCameraAngleX(Math.toRadians(r.getCameraAngleX()));
        c.setCameraAngleY(Math.toRadians(r.getCameraAngleY()));

        c.setWidth(parseWidth(r.getResolution()));
        c.setHeight(parseHeight(r.getResolution()));

        if (c.getResDir() == null) {
            c.setResDir(".");
        }
        return c;
    }


    private static int parseWidth(String resolution) {
        Integer w = null;
        try {
            String width = resolution.split(",")[0];
            w = Integer.parseInt(width);
            if (w < 1) {
                throw new IllegalArgumentException("not positive width: " + w);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("error parsing resolution width: " + resolution, e);
        }
        return w;
    }

    private static int parseHeight(String resolution) {
        Integer h = null;
        try {
            String height = resolution.split(",")[1];
            h = Integer.parseInt(height);
            if (h < 1) {
                throw new IllegalArgumentException("not positive height: " + h);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("error parsing resolution height: " + resolution, e);
        }
        return h;
    }


    private static void validate(CliRenderOptions result) {

        List<String> validate = CliRenderOptionsUtil.validate(result);
        if (validate.size() == 0) {
            return;
        }

        String msgs = "";
        for (String msg : validate) {
            if (!"".equals(msgs)) {
                msgs += "\n";
            }
            msgs += msg;
        }

        throw new ArgumentValidationException("Error parsing arguments:\n" + msgs);
    }


    private static void printHelp() {
        System.out.println("test");
    }
}
