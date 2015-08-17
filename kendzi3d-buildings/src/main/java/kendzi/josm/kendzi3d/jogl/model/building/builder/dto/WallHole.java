package kendzi.josm.kendzi3d.jogl.model.building.builder.dto;

import javax.vecmath.Point2d;

public class WallHole {
    private Point2d ldp;
    private Point2d rdp;
    private Point2d rtp;
    private Point2d ltp;

    /**
     * @return the ldp
     */
    public Point2d getLdp() {
        return ldp;
    }

    /**
     * @param ldp
     *            the ldp to set
     */
    public void setLdp(Point2d ldp) {
        this.ldp = ldp;
    }

    /**
     * @return the rdp
     */
    public Point2d getRdp() {
        return rdp;
    }

    /**
     * @param rdp
     *            the rdp to set
     */
    public void setRdp(Point2d rdp) {
        this.rdp = rdp;
    }

    /**
     * @return the rtp
     */
    public Point2d getRtp() {
        return rtp;
    }

    /**
     * @param rtp
     *            the rtp to set
     */
    public void setRtp(Point2d rtp) {
        this.rtp = rtp;
    }

    /**
     * @return the ltp
     */
    public Point2d getLtp() {
        return ltp;
    }

    /**
     * @param ltp
     *            the ltp to set
     */
    public void setLtp(Point2d ltp) {
        this.ltp = ltp;
    }
}