/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.selection;

import javax.vecmath.Point3d;

import kendzi.kendzi3d.editor.selection.SphereSelection;

public abstract class BuildingSelection extends SphereSelection {

    long wayId;

    public BuildingSelection(long wayId, Point3d center, double radius) {
        super(center, radius);

        this.wayId = wayId;
    }

    /**
     * @return the wayId
     */
    public long getWayId() {
        return wayId;
    }

    /**
     * @param wayId
     *            the wayId to set
     */
    public void setWayId(long wayId) {
        this.wayId = wayId;
    }
}
