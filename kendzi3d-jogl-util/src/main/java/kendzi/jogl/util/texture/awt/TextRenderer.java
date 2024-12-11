package kendzi.jogl.util.texture.awt;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.stb.STBEasyFont;

public class TextRenderer {

    private final Font font;

    public TextRenderer(Font font) {
        this.font = font;
    }

    public void begin3DRendering() {
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        final Color color = Color.WHITE;
        GL11.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
    }

    public void end3DRendering() {
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
    }

    public Rectangle2D getBounds(String text) {
        final int height = STBEasyFont.stb_easy_font_height(text);
        final int width = STBEasyFont.stb_easy_font_width(text);
        return new Rectangle2D.Double(0, 0, width, height);
    }

    public void draw3D(String text, float x, float y, float z, float scaleFactor) {
        final ByteBuffer charBuffer = BufferUtils.createByteBuffer(text.length() * 270);
        int quads = STBEasyFont.stb_easy_font_print(x, y, text, null, charBuffer);
        charBuffer.flip();
        GL11.glVertexPointer(2, GL11.GL_FLOAT, 16, charBuffer);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glRotatef(180, 1, 0, 0);
        GL11.glScalef(scaleFactor, scaleFactor, 1);
        GL11C.glDrawArrays(GL11.GL_QUADS, 0, quads * 4);
        GL11.glPopMatrix();
    }

    public void setUseVertexArrays(boolean b) {
        // Do nothing -- here for compatibility
    }
}