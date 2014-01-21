/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.ground;

import javax.media.opengl.GL2;
import javax.vecmath.Point3d;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.texture.TextureCacheService;
import kendzi.jogl.texture.TextureCacheServiceImpl;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.kendzi3d.josm.model.perspective.Perspective;

import com.jogamp.opengl.util.texture.Texture;

public class Ground {

    protected TextureCacheServiceImpl textureCacheService;

    private TextureLibraryStorageService textureLibraryStorageService;


    public Ground(TextureCacheService textureCacheService, TextureLibraryStorageService TextureLibraryStorageService) {
        super();
        if (textureCacheService instanceof TextureCacheServiceImpl) {
            this.textureCacheService = (TextureCacheServiceImpl) textureCacheService;
        } else {
            // XXX temporary
            throw new IllegalArgumentException("TextureLibraryStorageService has to be instance of TextureCacheServiceImpl but it is instance of: " + textureCacheService);
        }
        this.textureLibraryStorageService = TextureLibraryStorageService;
    }



    private Perspective pers;

    public void init() {

    }

    public void draw(GL2 gl , Camera camera, Perspective3D perspective3d ) {
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_TEXTURE_2D);

        //gl.glColor3f((float) 188/255, (float)169/255, (float)169/255);

        TextureData td = this.textureLibraryStorageService.getTextureDefault("ground.unknown");
        Texture texture = this.textureCacheService.get(gl, td.getTex0());

        texture.enable(gl);
        texture.bind(gl);


        gl.glBegin(GL2.GL_POLYGON);
        gl.glColor3f((float)123/256, (float)111/256, (float)100/255);

        double groundSize = 1500.0;

        Point3d c = camera.getPoint();




        //gl.glTexCoord2d(tc.left(), tc.bottom());
        double xRight = c.x + groundSize;
        double xLeft = c.x - groundSize;

        double zButtom = c.z + groundSize;
        double zTop = c.z - groundSize;

        gl.glNormal3d(0, 1, 0);

        gl.glTexCoord2d(xRight * td.getWidth() , zButtom * td.getHeight());
        gl.glVertex3d(xRight, -0.01, zButtom);
        gl.glTexCoord2d(xLeft * td.getWidth() , zButtom * td.getHeight());
        gl.glVertex3d(xLeft, -0.01, zButtom);
        gl.glTexCoord2d(xLeft * td.getWidth() , zTop * td.getHeight());
        gl.glVertex3d(xLeft, -0.01, zTop);
        gl.glTexCoord2d(xRight * td.getWidth() , zTop * td.getHeight());
        gl.glVertex3d(xRight, -0.01, zTop);

        gl.glEnd();

        texture.disable(gl);

        gl.glDisable(GL2.GL_TEXTURE_2D);
    }

}
