package kendzi.jogl;

import java.util.ArrayDeque;
import java.util.Deque;

import org.joml.Matrix4d;
import org.lwjgl.opengl.GL11;

/**
 * A class to help ease the transition off of deprecated GL matrix functions
 */
public class MatrixMath {
    private MatrixMath() {
        /* Do nothing */};

    private static final Deque<Matrix4d> stack = new ArrayDeque<>(64);
    private static final Matrix4d currentMatrix = new Matrix4d();

    public static Matrix4d glTranslated(double x, double y, double z) {
        GL11.glTranslated(x, y, z);
        return currentMatrix.translate(x, y, z);
    }

    public static void glPushMatrix() {
        GL11.glPushMatrix();
        stack.push(new Matrix4d(currentMatrix));
    }

    public static void glPopMatrix() {
        GL11.glPopMatrix();
        currentMatrix.set(stack.pop());
    }

    public static void glRotated(double angle, double x, double y, double z) {
        GL11.glRotated(angle, x, y, z);
        currentMatrix.rotate(angle, x, y, z);
    }
}
