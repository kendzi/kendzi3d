package kendzi.josm.kendzi3d.jogl.skybox;

import com.google.inject.Inject;
import com.jogamp.opengl.GL2;

import javax.vecmath.Point3d;

import kendzi.jogl.texture.TextureCacheService;
import kendzi.jogl.util.texture.Texture;
import kendzi.jogl.util.texture.TextureCoords;
import org.lwjgl.opengl.GL11;

/**
 * Drawer for skybox.
 */
public class SkyBoxDrawer {

    private final TextureCacheService textureCacheService;

    private SkyBoxConfiguration configuration;

    private final Point3d leftBottomBack = new Point3d(-10, -10, -10);
    private final Point3d rightBottomBack = new Point3d(10, -10, -10);
    private final Point3d rightTopBack = new Point3d(10, 10, -10);
    private final Point3d leftTopBack = new Point3d(-10, 10, -10);

    private final Point3d rightBottomFront = new Point3d(10, -10, 10);
    private final Point3d leftBottomFront = new Point3d(-10, -10, 10);
    private final Point3d leftTopFront = new Point3d(-10, 10, 10);
    private final Point3d rightTopFront = new Point3d(10, 10, 10);

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

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);

        // Set white color for texture
        GL11.glColor4f(1f, 1f, 1f, 1f);

        // Mix transparency color with texture
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);

        GL11.glPushMatrix();

        GL11.glTranslated(cameraLocation.x, cameraLocation.y, cameraLocation.z);

        GL11.glRotated(180d, 0, 1, 0);

        drawPolygon(gl, configuration.getFrontTexture(), leftBottomBack, rightBottomBack, rightTopBack, leftTopBack);

        drawPolygon(gl, configuration.getBackTexture(), rightBottomFront, leftBottomFront, leftTopFront, rightTopFront);

        drawPolygon(gl, configuration.getRightTexture(), rightBottomBack, rightBottomFront, rightTopFront, rightTopBack);

        drawPolygon(gl, configuration.getLeftTexture(), leftBottomFront, leftBottomBack, leftTopBack, leftTopFront);

        drawPolygon(gl, configuration.getTopTexture(), leftTopBack, rightTopBack, rightTopFront, leftTopFront);

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

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
            Texture texture = textureCacheService.getTexture(textureName);

            texture.enable(gl);
            texture.bind(gl);

            tc = texture.getImageTexCoords();
        }

        GL11.glBegin(GL11.GL_POLYGON);

        GL11.glTexCoord2d(tc.left(), tc.bottom());
        GL11.glVertex3d(p1.x, p1.y, p1.z);
        GL11.glTexCoord2d(tc.right(), tc.bottom());
        GL11.glVertex3d(p2.x, p2.y, p2.z);
        GL11.glTexCoord2d(tc.right(), tc.top());
        GL11.glVertex3d(p3.x, p3.y, p3.z);
        GL11.glTexCoord2d(tc.left(), tc.top());
        GL11.glVertex3d(p4.x, p4.y, p4.z);

        GL11.glEnd();

        if (textureName != null) {
            Texture t = textureCacheService.getTexture(textureName);
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
