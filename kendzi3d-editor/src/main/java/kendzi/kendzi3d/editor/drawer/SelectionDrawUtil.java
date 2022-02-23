package kendzi.kendzi3d.editor.drawer;

import com.jogamp.opengl.GL2;

import javax.vecmath.Point3d;

import kendzi.jogl.util.DrawUtil;
import kendzi.kendzi3d.editor.selection.Selectable;
import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.kendzi3d.editor.selection.SphereSelection;
import org.lwjgl.opengl.GL11;

public class SelectionDrawUtil {

    public static void drawSphereSelection(GL2 gl, Selectable r) {

        for (Selection selection : r.getSelection()) {
            if (selection instanceof SphereSelection) {

                SphereSelection s = (SphereSelection) selection;
                GL11.glPushMatrix();

                Point3d p = s.getCenter();

                double dx = p.x;
                double dy = p.y;
                double dz = p.z;

                GL11.glLineWidth(1);
                GL11.glTranslated(dx, dy, dz);

                DrawUtil.drawDotOuterY(gl, s.getRadius(), 24);

                GL11.glRotated(90d, 1d, 0, 0);
                DrawUtil.drawDotOuterY(gl, s.getRadius(), 24);

                GL11.glRotated(90d, 0, 0, 1d);
                DrawUtil.drawDotOuterY(gl, s.getRadius(), 24);

                GL11.glPopMatrix();
            }
        }
    }
}
