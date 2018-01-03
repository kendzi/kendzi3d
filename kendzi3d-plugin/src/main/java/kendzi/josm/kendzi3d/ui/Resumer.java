package kendzi.josm.kendzi3d.ui;

public interface Resumer {

    @FunctionalInterface
    public interface Resumable {

        public void resume();
    }

    default public void resumeResumable() {
        //
    }

    public void setResumable(Resumable r);
}
