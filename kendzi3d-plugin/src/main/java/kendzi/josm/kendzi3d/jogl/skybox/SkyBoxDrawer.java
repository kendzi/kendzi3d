package kendzi.josm.kendzi3d.jogl.skybox;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import javax.vecmath.Point3d;

import kendzi.jogl.texture.TextureCacheService;

import com.google.inject.Inject;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * Drawer for skybox.
 */
public class SkyBoxDrawer {

    private final TextureCacheService textureCacheService;

    private SkyBoxConfiguration configuration;

    private Point3d leftBottomBack = new Point3d(-10, -10, -10);
    private Point3d rightBottomBack = new Point3d(10, -10, -10);
    private Point3d rightTopBack = new Point3d(10, 10, -10);
    private Point3d leftTopBack = new Point3d(-10, 10, -10);

    private Point3d rightBottomFront = new Point3d(10, -10, 10);
    private Point3d leftBottomFront = new Point3d(-10, -10, 10);
    private Point3d leftTopFront = new Point3d(-10, 10, 10);
    private Point3d rightTopFront = new Point3d(10, 10, 10);

    /**
     * Constructor.
     *
     * @param configuration
     *            configuration with textures names
     * @param textureCacheService
     *            texture cache service
     */
    @Inject
    public SkyBoxDrawer(SkyBoxConfiguration configuration, TextureCacheService textureCacheService) {
        this.configuration = configuration;
        this.textureCacheService = textureCacheService;
    }

    /**
     * Draws skybox.
     *
     * @param gl
     *            gl
     * @param cameraLocation
     *            camera location
     */
    public void draw(GL2 gl, Point3d cameraLocation) {

        if (configuration == null) {
            return;
        }

        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glDisable(GLLightingFunc.GL_LIGHTING);

        // Set white color for texture
        gl.glColor4f(1f, 1f, 1f, 1f);

        // Mix transparency color with texture
        gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

        gl.glPushMatrix();

        gl.glTranslated(cameraLocation.x, cameraLocation.y, cameraLocation.z);

        gl.glRotated(180d, 0, 1, 0);

        drawPolygon(gl, configuration.getFrontTexture(), leftBottomBack, rightBottomBack, rightTopBack, leftTopBack);

        drawPolygon(gl, configuration.getBackTexture(), rightBottomFront, leftBottomFront, leftTopFront, rightTopFront);

        drawPolygon(gl, configuration.getRightTexture(), rightBottomBack, rightBottomFront, rightTopFront, rightTopBack);

        drawPolygon(gl, configuration.getLeftTexture(), leftBottomFront, leftBottomBack, leftTopBack, leftTopFront);

        drawPolygon(gl, configuration.getTopTexture(), leftTopBack, rightTopBack, rightTopFront, leftTopFront);

        gl.glPopMatrix();

        gl.glEnable(GLLightingFunc.GL_LIGHTING);
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_DEPTH_TEST);

    }

    /**
     * @param gl
     * @param textureName
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     */
    public void drawPolygon(GL2 gl, String textureName, Point3d p1, Point3d p2, Point3d p3, Point3d p4) {
        TextureCoords tc = new TextureCoords(0, 0, 1, 1);
        if (textureName != null) {
            Texture texture = textureCacheService.getTexture(gl, textureName);

            texture.enable(gl);
            texture.bind(gl);

            tc = texture.getImageTexCoords();
        }

        gl.glBegin(GL2.GL_POLYGON);

        gl.glTexCoord2d(tc.left(), tc.bottom());
        gl.glVertex3d(p1.x, p1.y, p1.z);
        gl.glTexCoord2d(tc.right(), tc.bottom());
        gl.glVertex3d(p2.x, p2.y, p2.z);
        gl.glTexCoord2d(tc.right(), tc.top());
        gl.glVertex3d(p3.x, p3.y, p3.z);
        gl.glTexCoord2d(tc.left(), tc.top());
        gl.glVertex3d(p4.x, p4.y, p4.z);

        gl.glEnd();

        if (textureName != null) {
            Texture t = textureCacheService.getTexture(gl, textureName);
            if (t != null) {
                t.disable(gl);
            }
        }
    }

    /**
     * @return the configuration
     */
    public SkyBoxConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration
     *            the configuration to set
     */
    public void setConfiguration(SkyBoxConfiguration configuration) {
        this.configuration = configuration;
    }
}
