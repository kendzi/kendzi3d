/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.service.TextureCacheService;
import kendzi.math.geometry.Normal;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

import com.jogamp.opengl.util.texture.Texture;

public class Fence extends AbstractModel {

    /** Log. */
    private static final Logger log = Logger.getLogger(Fence.class);

    Texture tex;

    List<Point2D.Double> list = new ArrayList<Point2D.Double>();
    float hight = 1;
    private Way way;

    float uvEnd [];
    double normal [][];
    float[] lightPos;

    public Fence(Way way, Perspective3D pers, float[] lightPos) {
        super(way, pers);
        this.way = way;
        this.lightPos = lightPos;

        this.list = new ArrayList<Point2D.Double>();

        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);

            double x = pers.calcX(node.getEastNorth().getX());
            double y = pers.calcY(node.getEastNorth().getY());

            this.list.add(new Point2D.Double(x, y));
            log.info("d x: " + x + " y: " + y);
        }

        this.tex = TextureCacheService.getTextureFromDir("fence_undefined.png");

    }


    @Override
    public void buildModel() {

        double textureLenght = 2.0;

        List<Point2D.Double> pointList = new ArrayList<Point2D.Double>();

        this.uvEnd = new float[this.way.getNodesCount()];
        this.normal = new double[this.way.getNodesCount()][];

        if (this.way.getNodesCount() > 0) {
            Point2D.Double beginPoint = null;
            //			pointList.add(beginPoint);

            for (int i = 0; i < this.way.getNodesCount(); i++) {



                Node node = this.way.getNode(i);

                double x = this.perspective.calcX(node.getEastNorth().getX());
                double y = this.perspective.calcY(node.getEastNorth().getY());

                // node.getCoor()
                Point2D.Double endPoint = new Point2D.Double(x, y);
                pointList.add(endPoint);
                log.info("d x: " + x + " y: " + y);


                if (beginPoint != null) {

                    double[] calcNormalNorm = Normal.calcNormalNorm(
                            beginPoint.getX(), 0.0f,  beginPoint.getY(),
                            endPoint.getX(), 0.0f,  endPoint.getY(),
                            beginPoint.getX(), 1.0, beginPoint.getY());

                    // fence is flat surface so we have to rotate normal vectors always in sun direction

                    // for now only in 2d, should be enough
                    double angleBetwenVector = Normal.angleBetwenVector(-this.lightPos[0], -this.lightPos[2],
                            calcNormalNorm[0], calcNormalNorm[2]);

                    if (angleBetwenVector < Math.PI / 2) {
                        // rotate to sun
                        calcNormalNorm[0] = -calcNormalNorm[0];
                        calcNormalNorm[1] = -calcNormalNorm[1];
                        calcNormalNorm[2] = -calcNormalNorm[2];
                    }


                    this.normal[i] = calcNormalNorm;

                    double dist = beginPoint.distance(x, y);

                    this.uvEnd[i] = (int) (dist / textureLenght);

                } else {
                    this.uvEnd[i] = 0;
                }

                beginPoint = endPoint;
            }
        }



        this.buildModel = true;
    }

    @Override
    public void draw(GL2 gl, Camera camera) {

        //		gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHTING);


        // do not draw the transparent parts of the texture
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        // don't show source alpha parts in the destination

        // determine which areas of the polygon are to be rendered
        gl.glEnable(GL2.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL2.GL_GREATER, 0); // only render if alpha > 0



        this.tex.setTexParameteri(GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        this.tex.setTexParameteri(GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

        // enable texturing and choose thetexture
        gl.glEnable(GL2.GL_TEXTURE_2D);
        // roadTex.enable();
        this.tex.bind();


        // replace the quad colours with the texture
        //		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);






        gl.glBegin(GL2.GL_QUADS);
        //		gl.glColor3b((byte)188, (byte)169, (byte)169);
        gl.glColor3f((float) 188/255, (float)169/255, (float)169/255);

        //		gl.glColor3f(0.5f, 1.0f, 0.5f);

        if (this.list.size() > 0) {
            Double last = this.list.get(0);

            for (int i =1; i < this.list.size(); i++) {
                Double p = this.list.get(i);



                //				double angleBetwenVector = Normal.angleBetwenVector(camera.xLookAt, camera.zLookAt, normal[i][0], normal[i][2]);
                //
                //				if (angleBetwenVector < Math.PI /2) {
                //					gl.glNormal3d(-normal[i][0], -normal[i][1], -normal[i][2]);
                //
                //				} else {
                //					gl.glNormal3d(normal[i][0], normal[i][1], normal[i][2]);
                //				}

                gl.glNormal3d(this.normal[i][0], this.normal[i][1], this.normal[i][2]);

                //				XXX stosowac float!
                //				gl.glTexCoord2f (tc.left(), tc.top());
                gl.glTexCoord2f(0, 1f);
                gl.glVertex3d( last.getX(), 0.0f,  -last.getY());

                gl.glTexCoord2f(0, 0.0f);
                gl.glVertex3d( last.getX(), this.hight, -last.getY());



                gl.glTexCoord2f(this.uvEnd[i], 0f);
                gl.glVertex3d( p.getX(), this.hight, -p.getY());
                gl.glTexCoord2f(this.uvEnd[i], 1f);
                gl.glVertex3d( p.getX(), 0.0f,  -p.getY());

                last = p;
            }
            //

        }
        gl.glEnd();

        gl.glDisable(GL2.GL_TEXTURE_2D);


    }
}
