package kendzi.josm.kendzi3d.jogl.layer;

import java.util.List;

import kendzi.josm.kendzi3d.jogl.model.Model;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;

import org.openstreetmap.josm.actions.search.SearchCompiler.Match;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

public interface Layer {

    Match getNodeMatcher();
    Match getWayMatcher();
    Match getRelationMatcher();

    boolean isVisible();

    List<Model> getModels();

    void addModel(Node node, Perspective3D pPerspective3D);

    void addModel(Way way, Perspective3D pPerspective3D);

    void addModel(Relation relation, Perspective3D pPerspective3D);
    void clear();
}
