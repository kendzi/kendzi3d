package kendzi.kendzi3d.tile.server.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kendzi.kendzi3d.render.tile.Tile;
import kendzi.kendzi3d.tile.server.service.RenderService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class TileController {

    /** Log. */
    private static final Logger log = Logger.getLogger(TileController.class);

    private static final String cacheDir = "/osm_dev/tiles_cache/{2}/{0}/{1}.png";

    private static final int EXPIRES_DATE = 20660;

    private int sleep = 0;

    @Autowired
    RenderService renderService;

    @RequestMapping(value="/home" )
    public ModelAndView test(HttpServletResponse response) throws IOException{

        log.info(this.renderService.hashCode());
        log.info(this.renderService.toString());

        return new ModelAndView("home");
    }

    @RequestMapping(value="/tilesAsync/{z}/{x}/{y}.png", method = RequestMethod.GET )
    public void tiles(
            @PathVariable Integer z,
            @PathVariable Integer x,
            @PathVariable Integer y,

            HttpServletResponse response) throws IOException{

        tiles(z, x, y, null, response);
    }

    @RequestMapping(value="/tilesAsync/{z}/{x}/{y}.png/{parm}", method = RequestMethod.GET )
    public void tiles(
            @PathVariable Integer z,
            @PathVariable Integer x,
            @PathVariable Integer y,
            @PathVariable String parm,

            HttpServletResponse response) {

        boolean dirty = false;
        boolean status = false;
        if ("dirty".equals(parm)) {
            dirty = true;
        } else if ("status".equals(parm)) {
            status = true;
        }

        if (z < 14) {
            throw new RuntimeException("zoom biger than 14");
        }

        log.info("render call for z: " + z + " x: " + x + " y: " + y + " dirty: " + dirty + " status: " + status);

        Tile tile = new Tile(x, y, z);

        byte [] tileBytes = null;

        if (dirty || !isInCache(tile)) {
            tileBytes = this.renderService.render(tile);
            saveToCache(tile, tileBytes);
        } else {
            tileBytes = loadFromCache(tile);
        }

        ByteArrayInputStream fileIn = new ByteArrayInputStream(tileBytes);

        response.setContentType("image/png");
        response.setContentLength(new Long(tileBytes.length).intValue());

        response.setHeader("Cache-Control","max-age=" + EXPIRES_DATE);
        response.setHeader("Expires", htmlExpiresDate(EXPIRES_DATE));

        try {
            FileCopyUtils.copy(fileIn, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String htmlExpiresDate(int seconds) {

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.SECOND, seconds);

        return formatHtmlExpiresDate().format(cal.getTime());
    }

    public static DateFormat formatHtmlExpiresDate() {
        DateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return httpDateFormat;
    }

    @RequestMapping( value="/sleep", method = RequestMethod.GET)
    @Async
    public Callable<ModelAndView> sleep() {

        final int s = this.sleep++;
        log.info("call action sleep: " + s);

        return new Callable<ModelAndView>() {

            @Override
            public ModelAndView call() throws Exception {

                log.info("start sleep: " + s);
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {};

                log.info("end sleep: " + s);

                return new ModelAndView("home");
            }
        };
    }


    @RequestMapping(value="/sleep2", method = RequestMethod.GET )
    public ModelAndView sleep2(HttpServletRequest req, HttpServletResponse response) {

        final int s = this.sleep++;
        log.info("call action sleep: " + s);

        log.info("start sleep: " + s);
        try {
            Thread.sleep(10000);
        } catch (Exception e) {};

        log.info("end sleep: " + s);

        return new ModelAndView("home");
    }




    @RequestMapping(value="/tiles/{z}/{x}/{y}.png", method = RequestMethod.GET )
    public Callable<HttpEntity<byte[]>> tiles2(
            @PathVariable Integer z,
            @PathVariable Integer x,
            @PathVariable Integer y,

            HttpServletResponse response) {

        return tiles2(z, x, y, null, response);
    }


    @RequestMapping(value="/tiles/{z}/{x}/{y}.png/{parm}", method = RequestMethod.GET )
    public Callable<HttpEntity<byte[]>> tiles2(
            @PathVariable Integer z,
            @PathVariable Integer x,
            @PathVariable final Integer y,
            @PathVariable String parm,

            HttpServletResponse response) {

        boolean dirty = false;
        boolean status = false;
        if ("dirty".equals(parm)) {
            dirty = true;
        } else if ("status".equals(parm)) {
            status = true;
        }

        final boolean isDirty = dirty;

        if (z < 14) {
            throw new RuntimeException("zoom biger than 14");
        }

        log.info("z: " + z + " x: " + x + " y: " + y);

        final Tile tile = new Tile(x, y, z);

        return new Callable<HttpEntity<byte[]>>() {

            @Override
            public HttpEntity<byte[]> call() throws Exception {

                byte [] tileBytes = null;

                if (isDirty || !isInCache(tile)) {
                    tileBytes = TileController.this.renderService.render(tile);
                    saveToCache(tile, tileBytes);
                } else {
                    tileBytes = loadFromCache(tile);
                }

                final byte [] documentBody = tileBytes;

                HttpHeaders header = new HttpHeaders();
                header.setContentType(new MediaType("image", "png"));

                header.setContentLength(documentBody.length);





                header.set("Cache-Control","max-age=" + EXPIRES_DATE);
                header.set("Expires", htmlExpiresDate(EXPIRES_DATE));

                return new HttpEntity<byte[]>(documentBody, header);
            }
        };
    }

    private void saveToCache(Tile tile, byte[] tileBytes) {

        if (tileBytes == null) {
            throw new RuntimeException("tileBytes cant be null!");
        }

        ByteArrayInputStream in = new ByteArrayInputStream(tileBytes);

        File f = new File(getFileName(tile));

        try {
            File parent = f.getParentFile();
            parent.mkdirs();

            FileOutputStream out = new FileOutputStream(f);
            FileCopyUtils.copy(in, out);

        } catch (IOException e) {
            log.error("Error saving file to cache", e);
        }
    }

    private byte[] loadFromCache(Tile tile) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        File f = new File(getFileName(tile));


        try {
            FileInputStream in = new FileInputStream(f);
            FileCopyUtils.copy(in, out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error loading file from cache", e);
        }
        return null;
    }


    private boolean isInCache(Tile tile) {
        File f = new File(getFileName(tile));

        return f.exists();
    }

    private String getFileName(Tile t) {
        return MessageFormat.format(
                this.cacheDir,
                Long.toString(t.getX()),
                Long.toString(t.getY()),
                Long.toString(t.getZ()));
    }
}
