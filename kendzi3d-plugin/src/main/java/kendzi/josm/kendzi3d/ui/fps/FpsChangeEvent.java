/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.ui.fps;

/**
 * Event fired when fps value is calculated.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class FpsChangeEvent {
    int fps;
    long time;

    /** Constructor.
     * @param fps fps
     * @param time time when application is run
     */
    public FpsChangeEvent(int fps, long time) {
        super();
        this.fps = fps;
        this.time = time;
    }

    /**
     * @return the fps
     */
    public int getFps() {
        return this.fps;
    }
    /**
     * @param fps the fps to set
     */
    public void setFps(int fps) {
        this.fps = fps;
    }
    /**
     * @return the time
     */
    public long getTime() {
        return this.time;
    }
    /**
     * @param time the time to set
     */
    public void setTime(long time) {
        this.time = time;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FpsChangeEvent [fps=" + this.fps + ", time=" + this.time + "]";
    }
}
