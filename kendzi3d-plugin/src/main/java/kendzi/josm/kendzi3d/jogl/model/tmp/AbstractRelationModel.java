/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.tmp;

import kendzi.josm.kendzi3d.jogl.model.AbstractModel;
import kendzi.kendzi3d.josm.model.perspective.Perspective;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;

public abstract class AbstractRelationModel extends AbstractModel {

    protected Relation relation;

    public AbstractRelationModel(Relation pRelation, Perspective pers) {
        super(pers);

        relation = pRelation;
    }

    @Override
    public void rebuildWorldObject(OsmPrimitive primitive, Perspective perspective) {
        // clean up everything
        relation = (Relation) primitive;
        this.perspective = perspective;

        buildWorldObject();
    }
}
