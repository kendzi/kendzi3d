package kendzi.josm.kendzi3d.data.event;

import kendzi.josm.kendzi3d.ui.Resumer;

public abstract class DataEvent implements Resumer {

    private Resumable resumable = () -> {};

    @Override
    public void resumeResumable() {
        resumable.resume();
    }

    @Override
    public void setResumable(Resumable r) {
        resumable = r;
    }
}
