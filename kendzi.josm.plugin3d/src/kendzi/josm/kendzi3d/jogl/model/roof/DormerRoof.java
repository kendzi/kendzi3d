/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.DrawUtil;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.model.Building;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.DormerRoofBuilder;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofDebugOut;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementParserUtil;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Way;

import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Represent flat roof.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public class DormerRoof extends Roof {

    /** Log. */
    private static final Logger log = Logger.getLogger(DormerRoof.class);


    /** XXX
     * Font for axis.
     */
    private Font font = new Font("SansSerif", Font.BOLD, 24);

    /** XXX
     * For axis labels.
     */
    private TextRenderer axisLabelRenderer = new TextRenderer(this.font);

    /** XXX
     * For the axis labels.
     */
    private final static float SCALE_FACTOR = 0.01f;


    ModelRender modelRender;
//    private Model model;




    private RoofDebugOut debug;


    private List<Point3d> biggerRec;


    /** Flat Roof.
     * @param pBuilding building
     * @param pList list of building walls
     * @param pWay way
     * @param pPerspective perspective
     */
    public DormerRoof(Building pBuilding, List<Point2d> pList, Way pWay, Perspective3D pPerspective) {
        super(pBuilding, pList, pWay, pPerspective);


        //
        //        list = new ArrayList<Point2D.Double>();
        //
        //        for (int i = 0; i < pWay.getNodesCount(); i++) {
        //            Node node = pWay.getNode(i);
        //
        //            double x = pPerspective.calcX(node.getEastNorth().getX());
        //            double y = pPerspective.calcY(node.getEastNorth().getY());
        //
        //            list.add(new Point2D.Double(x, y));
        //        }

        //        height = ModelUtil.getHeight(pWay, 1);
        this.height = pBuilding.getHeight();

        this.modelRender = ModelRender.getInstance();
    }




    @Override
    public void buildModel() {

        String type = this.way.get("3dr:type");
        String dormer = this.way.get("3dr:dormer");

        String dormerWidth = this.way.get("3dr:dormer:width");
        String dormerHeight = this.way.get("3dr:dormer:heightX");
        String dormerLenght = this.way.get("3dr:dormer:lenghtX");

        Map<MeasurementKey, Measurement> measurements = parseMeasurements(this.way);



        List<Double> heights = toDouble(getList(this.way, "3dr:height"));

        List<Double> sizeB = toDouble(getList(this.way, "3dr:lenght"));

        RoofTextureData rtd = new RoofTextureData();
        rtd.setFacadeTextrure(getFasadeTexture());
        rtd.setRoofTexture(getRoofTexture());



        RoofOutput roofOutput = DormerRoofBuilder.build(this.list.get(0), this.list, type, dormer, this.height, heights,
                sizeB, measurements, rtd);

        this.debug = roofOutput.getDebug();

        this.minHeight = this.height - roofOutput.getHeight();
        this.model = roofOutput.getModel();

    }



    /**
     * Take measurements from way.
     * @param pWay way
     * @return measurements
     */
    private Map<MeasurementKey, Measurement> parseMeasurements(Way pWay) {

        Map<MeasurementKey, Measurement> ret = new HashMap<MeasurementKey, Measurement>();

        for (MeasurementKey key : MeasurementKey.values()) {

            String value = pWay.get(key.getKey());
            Measurement measurement = MeasurementParserUtil.parse(value);

            ret.put(key, measurement);
        }

        return ret;
    }




    private List<Double> toDouble(List<String> strList) {
        List<Double> ret = new ArrayList<Double>();
        for (String string : strList) {
            Double number = null;
            try {
                // FIXME !!!!!!!!!!
                number = Double.parseDouble(string);
            } catch (Exception e) {
                log.error("error parsing double: " + string, e);
            }
            ret.add(number);
        }
        return ret;
    }




    private List<String> getList(Way way, String string) {
        List<String> ret = new ArrayList<String>();

        for (int i = 1; i < 10; i++) {
            ret.add(way.get(string + i));
        }
        return ret;
    }



    @Override
    public void draw(GL2 pGl, Camera pCamera) {
        this.modelRender.render(pGl, this.model);

        if (this.modelRender.isDebugging() && this.debug != null) {

            pGl.glDisable(GL2.GL_LIGHTING);

            // red
            pGl.glColor3f(1.0f, 0f, 0f);

            // Set line width to 4
            pGl.glLineWidth(6);
            // Repeat count, repeat pattern
            pGl.glLineStipple(1, (short) 0xf0f0);

            pGl.glBegin(GL2.GL_LINE_LOOP);

            List<Point3d> rectangle = biggerRect();

            for (int i = 0; i < rectangle.size(); i++) {

                Point3d point3d = rectangle.get(i);

                pGl.glVertex3d(point3d.x, point3d.y, point3d.z);


            }
            pGl.glEnd();

            for (int i = 0; i < rectangle.size(); i++) {
                Point3d point3d = rectangle.get(i);
                drawAxisText(pGl, ("rec point " + (i + 1)), point3d.x, point3d.y, point3d.z);
            }

            Point2d point2d = this.list.get(0);

            float[] rgba = new float[4];
            // green
            pGl.glColor3fv(Color.RED.darker().getRGBComponents(rgba), 0);

            double x = point2d.x;
            double y = this.minHeight;
            double z = -point2d.y;
            double d = 0.25;

//            pGl.glBegin(GL2.GL_LINE_LOOP);
//
//            pGl.glVertex3d(x + d, y, z);
//            pGl.glVertex3d(x, y, z - d);
//            pGl.glVertex3d(x - d, y, z);
//            pGl.glVertex3d(x, y, z + d);
//
//            pGl.glEnd();

            pGl.glPushMatrix();

            pGl.glTranslated(x, y, z);

            DrawUtil.drawDotY(pGl, d, 12);

            pGl.glPopMatrix();

        }
    }


    private List<Point3d> biggerRect() {
        if (this.biggerRec != null) {
            return this.biggerRec;

        }
        List<Point3d> ret = new ArrayList<Point3d>();
        double middleX = 0;
        double middleY = 0;
        double middleZ = 0;

        for (Point3d p :  this.debug.getRectangle()) {
            middleX = middleX + p.x;
            middleY = middleY + p.y;
            middleZ = middleZ + p.z;
        }

        Point3d middle = new Point3d(
                middleX / this.debug.getRectangle().size(),
                middleY / this.debug.getRectangle().size(),
                middleZ / this.debug.getRectangle().size());

        for (Point3d p :  this.debug.getRectangle()) {
            Vector3d v = new Vector3d(p);
            v.sub(middle);
            v.normalize();
            v.scale(0.1d);

            Point3d bigger= new Point3d(p);
            bigger.add(v);
            ret.add(bigger);
        }
        this.biggerRec = ret;
        return this.biggerRec;
    }




    /**
     * Draw txt at (x,y,z), with the text centered in the x-direction, facing
     * along the +z axis.
     *
     * @param gl
     * @param txt
     * @param x
     * @param y
     * @param z
     */
    private void drawAxisText(GL2 gl, String txt, double x, double y, double z) {

        Rectangle2D dim = this.axisLabelRenderer.getBounds(txt);
        float width = (float) dim.getWidth() * SCALE_FACTOR;

        this.axisLabelRenderer.begin3DRendering();
        this.axisLabelRenderer.draw3D(txt, (float) x - width / 2, (float)y, (float)z, SCALE_FACTOR);
        this.axisLabelRenderer.end3DRendering();
    }

}
