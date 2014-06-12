package kendzi.josm.kendzi3d.jogl.layer;

import java.util.ArrayList;
import java.util.List;

import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.WorldObject;
import kendzi.kendzi3d.world.quad.layer.LayerMatcher;
import kendzi.kendzi3d.world.quad.layer.ModelBuilder;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

public abstract class LayerContainer2 implements LayerMatcher, ModelBuilder {

    /**
     * List of layer models.
     */
    private List<WorldObject> modelList = new ArrayList<WorldObject>();

    public List<WorldObject> getModels() {
        return this.modelList;
    }

    public void addModel(Node node, Perspective perspective) {
        modelList.add(buildModel(node, perspective));
    }

    public void addModel(Way way, Perspective perspective) {
        modelList.add(buildModel(way, perspective));
    }

    public void addModel(Relation relation, Perspective perspective) {
        modelList.add(buildModel(relation, perspective));
    }

    public void clear() {
        this.modelList.clear();
    }
}
