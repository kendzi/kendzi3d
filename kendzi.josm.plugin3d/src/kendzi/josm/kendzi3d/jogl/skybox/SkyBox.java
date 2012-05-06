package kendzi.josm.kendzi3d.jogl.skybox;

import javax.media.opengl.GL2;
import javax.vecmath.Point3d;

import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.service.TextureCacheService;
import kendzi.josm.kendzi3d.service.TextureLibraryService;

import com.google.inject.Inject;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

public class SkyBox {

    @Inject
    private TextureCacheService textureCacheService;

    @Inject
    private TextureLibraryService textureLibraryService;

    public void init() {

    }

    public void draw(GL2 gl , Camera camera, Perspective3D perspective3d) {

        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glColor4f((float) 255/255, (float)255/255, (float)255/255, 1f);

        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_TEXTURE_2D);

        //XXX
        String frontTextureName = "/textures/free/sky1/sfront37.jpg";
        String rightTextureName = "/textures/free/sky1/sright37.jpg";
        String leftTextureName = "/textures/free/sky1/sleft37.jpg";
        String backTextureName = "/textures/free/sky1/sback37.jpg";
        String topTextureName = "/textures/free/sky1/stop37.jpg";

        gl.glPushMatrix();
        Point3d p = camera.getPoint();

        gl.glTranslated(p.x, p.y, p.z);

        drawPolygon(gl, frontTextureName,
                new Point3d(-10, -10, -10),
                new Point3d(10, -10, -10),
                new Point3d(10, 10, -10),
                new Point3d(-10, 10, -10));
        drawPolygon(gl, backTextureName,
                new Point3d(10, -10, 10),
                new Point3d(-10, -10, 10),
                new Point3d(-10, 10, 10),
                new Point3d(10, 10, 10));
//
        drawPolygon(gl, rightTextureName,
                new Point3d(10, -10, -10),
                new Point3d(10, -10,  10),
                new Point3d(10, 10,  10),
                new Point3d(10, 10, -10));
//
        drawPolygon(gl, leftTextureName,
                new Point3d(-10, -10, 10),
                new Point3d(-10, -10, -10),
                new Point3d(-10, 10, -10),
                new Point3d(-10, 10, 10));

        drawPolygon(gl, topTextureName,
                new Point3d(-10, 10, -10),
                new Point3d(10, 10, -10),
                new Point3d(10, 10, 10),
                new Point3d(-10, 10, 10));

        gl.glPopMatrix();
        gl.glDisable(GL2.GL_TEXTURE_2D);


        gl.glColor4f((float) 255/255, (float)255/255, (float)255/255, (float) 255/255);

        gl.glEnable(GL2.GL_DEPTH_TEST);

    }

    /**
     * @param gl
     * @param frontTextureName
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     */
    public void drawPolygon(GL2 gl, String frontTextureName, Point3d p1, Point3d p2, Point3d p3, Point3d p4) {
        TextureCoords tc = new TextureCoords(0, 0, 1, 1);
        if (frontTextureName!= null) {
            Texture texture = this.textureCacheService.getTexture(frontTextureName);

            texture.enable();
            texture.bind();

            tc = texture.getImageTexCoords();
        }

        gl.glBegin(GL2.GL_POLYGON);

        gl.glTexCoord2d(tc.left(), tc.bottom());
        gl.glVertex3d(p1.x ,p1.y, p1.z);
        gl.glTexCoord2d(tc.right(), tc.bottom());
        gl.glVertex3d(p2.x ,p2.y, p2.z);
        gl.glTexCoord2d(tc.right(), tc.top());
        gl.glVertex3d(p3.x ,p3.y, p3.z);
        gl.glTexCoord2d(tc.left() , tc.top());
        gl.glVertex3d(p4.x ,p4.y, p4.z);

        gl.glEnd();


//        gl.glColor3f((float) 0/255, (float)0/255, (float)255/255);

//        gl.glPushMatrix();
//        gl.glTranslated(x, 0.1, z);
//        DrawUtil.drawDotY(gl, 0.5, 12);
//        gl.glPopMatrix();

        if (frontTextureName!= null) {
            Texture t = this.textureCacheService.getTexture(frontTextureName);
            if (t != null) {
                t.disable();
            }
        }
    }
}
