package kendzi.kendzi3d.editor.drawer;

import com.jogamp.opengl.GL2;
import javax.vecmath.Point3d;

import kendzi.jogl.util.DrawUtil;
import kendzi.kendzi3d.editor.selection.Selectable;
import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.kendzi3d.editor.selection.SphereSelection;

public class SelectionDrawUtil {

    public static void drawSphereSelection(GL2 gl, Selectable r) {

        for (Selection selection : r.getSelection()) {
            if (selection instanceof SphereSelection) {

                SphereSelection s = (SphereSelection) selection;
                gl.glPushMatrix();

                Point3d p = s.getCenter();

                double dx = p.x;
                double dy = p.y;
                double dz = p.z;

                gl.glLineWidth(1);
                gl.glTranslated(dx, dy, dz);

                DrawUtil.drawDotOuterY(gl, s.getRadius(), 24);

                gl.glRotated(90d, 1d, 0, 0);
                DrawUtil.drawDotOuterY(gl, s.getRadius(), 24);

                gl.glRotated(90d, 0, 0, 1d);
                DrawUtil.drawDotOuterY(gl, s.getRadius(), 24);

                gl.glPopMatrix();
            }
        }
    }
}
