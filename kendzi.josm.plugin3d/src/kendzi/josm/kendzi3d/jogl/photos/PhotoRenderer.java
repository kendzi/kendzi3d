/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.photos;

import javax.media.opengl.GL2;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.DrawUtil;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.service.TextureCacheService;
import kendzi.math.geometry.point.PointUtil;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.projection.Projection;

import com.google.inject.Inject;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

public class PhotoRenderer {

//    private Perspective3D pers;

    private Photo photo;
    private PhotoCache photoCache;

    private boolean enabled;


    @Inject
    private TextureCacheService textureCacheService;// = ApplicationContextUtil.getTextureCacheService();

//    Ground(Perspective3D pers) {
//        this.pers = pers;
//    }

    public void init() {

    }

    public void draw(GL2 gl , Camera camera, Perspective3D perspective3d) {
        Photo photo =  this.photo ;


        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glColor4f((float) 255/255, (float)255/255, (float)255/255, (float) photo.getTransparent());

        // Enable Alpha Blending (disable alpha testing)
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL2.GL_BLEND);



        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_TEXTURE_2D);


        //XXX
        String textureName = photo.getPath();

        TextureCoords tc = new TextureCoords(0, 0, 1, 1);
        if (textureName!= null) {
            Texture texture = this.textureCacheService.getTexture(textureName);

//            // switch to texture mode and push a new matrix on the stack
//            gl.glMatrixMode(GL2.GL_TEXTURE);
//            gl.glPushMatrix();
//
//            // check to see if the texture needs flipping
//            if (texture.getMustFlipVertically()) {
//                gl.glScaled(1, -1, 1);
//                gl.glTranslated(0, -1, 0);
//            }
//
//            // switch to modelview matrix and push a new matrix on the stack
//            gl.glMatrixMode(GL2.GL_MODELVIEW);
//            gl.glPushMatrix();
//
//            // This is required to repeat textures
//            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
//            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

            // enable, bind
            texture.enable();
            texture.bind();

            tc = texture.getImageTexCoords();

        }




        gl.glBegin(GL2.GL_POLYGON);
//        gl.glColor3f((float)123/256, (float)111/256, (float)100/255);
//        gl.glColor3f((float) 255/255, (float)255/255, (float)255/255);

        LatLon ll = new LatLon(photo.getLat(), photo.getLon());

        Projection proj = Main.getProjection();

        EastNorth eastNorth = proj.latlon2eastNorth(ll);

        double x = perspective3d.calcX(eastNorth.east());
        double y = photo.getHeight();
        double z = - perspective3d.calcY(eastNorth.north());

        Vector3d angle = photo.getRotate();

        double distance = 500d;

        double width = distance * Math.sin(photo.getAngleWitht() / 2d);
        double height = distance * Math.sin(photo.getAngleHeigth() / 2d);


        Vector3d p1 = new Vector3d(distance, -height, -width);
        Vector3d p2 = new Vector3d(distance, -height, width);
        Vector3d p3 = new Vector3d(distance, height, width);
        Vector3d p4 = new Vector3d(distance, height, -width);

        p1 = transform(angle, p1);
        p2 = transform(angle, p2);
        p3 = transform(angle, p3);
        p4 = transform(angle, p4);


        Point3d c = camera.getPoint();


       // gl.glColor4f((float) 255/255, (float)255/255, (float)255/255, (float) 128/255);
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_BLEND);

        gl.glTexCoord2d(tc.left(), tc.bottom());
        gl.glVertex3d(p1.x + x, p1.y + y, p1.z + z);
        gl.glTexCoord2d(tc.right(), tc.bottom());
        gl.glVertex3d(p2.x + x, p2.y + y, p2.z + z);
        gl.glTexCoord2d(tc.right(), tc.top());
        gl.glVertex3d(p3.x + x, p3.y + y, p3.z + z);
        gl.glTexCoord2d(tc.left() , tc.top());
        gl.glVertex3d(p4.x + x, p4.y + y, p4.z + z);

        gl.glEnd();


        gl.glColor3f((float) 0/255, (float)0/255, (float)255/255);

        gl.glPushMatrix();

        gl.glTranslated(x, 0.1, z);

        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

        DrawUtil.drawDotY(gl, 0.5, 12);
        gl.glPopMatrix();

        if (textureName!= null) {
            Texture texture = this.textureCacheService.getTexture(textureName);

            Texture t = this.textureCacheService.getTexture(textureName);
            //this.textures.get(mesh.materialID);// .get(mesh.materialID);
            if (t != null) {
                t.disable();
            }

            gl.glMatrixMode(GL2.GL_TEXTURE);
            gl.glPopMatrix();

            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glPopMatrix();
        }

        gl.glDisable(GL2.GL_TEXTURE_2D);


        gl.glColor4f((float) 255/255, (float)255/255, (float)255/255, (float) 255/255);

        gl.glDisable(GL2.GL_BLEND);
        gl.glEnable(GL2.GL_DEPTH_TEST);

    }

    private Vector3d transform(Vector3d angle, Vector3d speed) {
                speed = PointUtil.rotateX3d(speed, angle.getX());
                speed = PointUtil.rotateZ3d(speed, angle.getZ());
                speed = PointUtil.rotateY3d(speed, angle.getY());

                return speed;
    }

    class PhotoCache {

    }

    public void setPhoto(Photo pPhoto) {
        if (pPhoto == null) {
            this.enabled = false;
            // XXX multi thread !!!
            this.photoCache = null;
            this.photo = null;
            return;
        }

        calcCache(pPhoto);
        this.photo = pPhoto;
        this.enabled = true;
    }

    private void calcCache(Photo pPhoto) {

        PhotoCache photoCache = new PhotoCache();



        this.photoCache = photoCache;
        this.photo = pPhoto;
    }

    public void update(Camera camera, Perspective3D perspective3d) {

        Point3d cameraPoint = camera.getPoint();
        Vector3d cameraRotation = camera.getAngle();

        Perspective3D perspective = perspective3d;

        EastNorth eastNorth = perspective.toEastNorth(cameraPoint.x, -cameraPoint.z);

        Projection proj = Main.getProjection();

        LatLon latLon = proj.eastNorth2latlon(eastNorth);

        // XXX update cache
        this.photo.setLat(latLon.lat());
        this.photo.setLon(latLon.lon());
        this.photo.setHeight(cameraPoint.y);

        this.photo.setRotate(cameraRotation.x, cameraRotation.y  ,cameraRotation.z);

    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * @return the textureCacheService
     */
    public TextureCacheService getTextureCacheService() {
        return textureCacheService;
    }

    /**
     * @param textureCacheService the textureCacheService to set
     */
    public void setTextureCacheService(TextureCacheService textureCacheService) {
        this.textureCacheService = textureCacheService;
    }

}
