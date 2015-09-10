/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.editor.drawer;

import javax.inject.Inject;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;

import kendzi.jogl.Gl2Draw;
import kendzi.jogl.texture.TextureCacheService;
import kendzi.jogl.util.DrawUtil;
import kendzi.kendzi3d.editor.selection.ViewportProvider;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * Displays given icon.
 */
public class SquareIcon implements Gl2Draw {

    /**
     * File with icon.
     */
    private String icon;

    /**
     * Display icon size.
     */
    private double size = 64;

    private double x = 10;

    private double y = 10;

    /**
     * Texture cache service.
     */
    @Inject
    private TextureCacheService textureCacheService;

    /**
     * Provider for current viewport settings.
     */
    @Inject
    private ViewportProvider viewportProvider;

    /**
     * Adds icon to display.
     *
     * @param iconName
     *            resource path for file with icon
     */
    public void addIcon(String iconName) {
        icon = iconName;
    }

    /**
     * Draws icon.
     *
     * @param gl
     *            gl2
     */

    @Override
    public void draw(GL2 gl) {

        if (icon == null) {
            return;
        }

        // Calculate icon location on screen.
        double width = viewportProvider.getViewport().getWidth();
        double height = viewportProvider.getViewport().getHeight();

        double maxx = x + size;
        double minx = maxx - size;

        double miny = y;
        double maxy = miny + size;

        Texture texture = textureCacheService.getTexture(gl, icon);
        if (texture == null) {
            return;
        }
        texture.enable(gl);
        texture.bind(gl);

        gl.glDisable(GLLightingFunc.GL_LIGHTING);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        // Set transparency for texture
        gl.glColor4f(1f, 1f, 1f, 0.8f);

        // Mix transparency color with texture
        gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);

        // No depth.
        gl.glDisable(GL.GL_DEPTH_TEST);

        // Draw icon in 2d mode.
        DrawUtil.begin2D(gl, width, height);

        TextureCoords tc = texture.getImageTexCoords();

        gl.glBegin(GL2.GL_POLYGON);

        gl.glTexCoord2d(tc.left(), tc.bottom());
        gl.glVertex2d(minx, maxy);
        gl.glTexCoord2d(tc.right(), tc.bottom());
        gl.glVertex2d(maxx, maxy);
        gl.glTexCoord2d(tc.right(), tc.top());
        gl.glVertex2d(maxx, miny);
        gl.glTexCoord2d(tc.left(), tc.top());
        gl.glVertex2d(minx, miny);

        gl.glEnd();

        gl.glEnable(GLLightingFunc.GL_LIGHTING);

        texture.disable(gl);

        DrawUtil.end2D(gl);

        gl.glEnable(GL.GL_DEPTH_TEST);
    }
}
