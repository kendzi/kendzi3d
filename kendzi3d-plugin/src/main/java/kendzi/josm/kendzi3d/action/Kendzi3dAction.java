package kendzi.josm.kendzi3d.action;

public interface Kendzi3dAction {

    @FunctionalInterface
    public interface ResumableCanvas {

        public void resume();
    }

    public void setResumableCanvas(ResumableCanvas canvas);
}
