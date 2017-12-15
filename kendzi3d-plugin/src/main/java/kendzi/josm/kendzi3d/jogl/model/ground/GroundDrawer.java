/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.ground;

import com.jogamp.opengl.GL2;
import javax.vecmath.Point3d;

import kendzi.jogl.texture.TextureCacheService;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.TextureLibraryStorageService;

import com.jogamp.opengl.util.texture.Texture;

public class GroundDrawer {

    protected TextureCacheService textureCacheService;

    private final TextureLibraryStorageService textureLibraryStorageService;

    public GroundDrawer(TextureCacheService textureCacheService,
            TextureLibraryStorageService TextureLibraryStorageService) {
        super();

        this.textureCacheService = textureCacheService;
        this.textureLibraryStorageService = TextureLibraryStorageService;
    }

    public void init() {
        //
    }

    public void draw(GL2 gl, Point3d cameraPosition) {
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_TEXTURE_2D);

        TextureData td = this.textureLibraryStorageService.getTextureDefault("ground.unknown");
        Texture texture = this.textureCacheService.getTexture(gl, td.getTex0());

        texture.enable(gl);
        texture.bind(gl);

        gl.glBegin(GL2.GL_POLYGON);
        gl.glColor3f((float) 123 / 256, (float) 111 / 256, (float) 100 / 255);

        double groundSize = 1500.0;

        Point3d c = cameraPosition;

        // gl.glTexCoord2d(tc.left(), tc.bottom());
        double xRight = c.x + groundSize;
        double xLeft = c.x - groundSize;

        double zButtom = c.z + groundSize;
        double zTop = c.z - groundSize;

        gl.glNormal3d(0, 1, 0);

        gl.glTexCoord2d(xRight * td.getWidth(), zButtom * td.getHeight());
        gl.glVertex3d(xRight, -0.01, zButtom);
        gl.glTexCoord2d(xLeft * td.getWidth(), zButtom * td.getHeight());
        gl.glVertex3d(xLeft, -0.01, zButtom);
        gl.glTexCoord2d(xLeft * td.getWidth(), zTop * td.getHeight());
        gl.glVertex3d(xLeft, -0.01, zTop);
        gl.glTexCoord2d(xRight * td.getWidth(), zTop * td.getHeight());
        gl.glVertex3d(xRight, -0.01, zTop);

        gl.glEnd();

        texture.disable(gl);

        gl.glDisable(GL2.GL_TEXTURE_2D);
    }

}
