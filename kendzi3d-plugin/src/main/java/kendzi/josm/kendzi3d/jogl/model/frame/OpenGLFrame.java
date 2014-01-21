package kendzi.josm.kendzi3d.jogl.model.frame;


/**
 * Transform points from and to opengl frame.
 *
 * @author Tomasz Kedziora
 */
public interface OpenGLFrame {

    double getOpenGlX();
    double getOpenGlY();

    void setOpenGlX(double pX);
    void setOpenGlY(double pX);
}
