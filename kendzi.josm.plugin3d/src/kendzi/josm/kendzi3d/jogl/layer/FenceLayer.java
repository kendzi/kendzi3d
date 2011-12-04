/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.layer;

import java.util.ArrayList;
import java.util.List;

import kendzi.josm.kendzi3d.jogl.model.Fence2;
import kendzi.josm.kendzi3d.jogl.model.FenceRelation;
import kendzi.josm.kendzi3d.jogl.model.Model;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.actions.search.SearchCompiler;
import org.openstreetmap.josm.actions.search.SearchCompiler.Match;
import org.openstreetmap.josm.actions.search.SearchCompiler.ParseError;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

public class FenceLayer implements Layer {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(FenceLayer.class);

    /**
     * List of layer models.
     */
    private List<Model> modelList = new ArrayList<Model>();


    private Match fenceMatcher;
    private Match fenceRelationMatcher;

    {
        try {
            this.fenceMatcher = SearchCompiler.compile("(barrier=fence) | (barrier\\:part=fence)", false, false);
            this.fenceRelationMatcher = SearchCompiler.compile("((type=way\\:3d) & (barrier=fence))", false, false);
        } catch (ParseError e) {
            this.fenceMatcher = new SearchCompiler.Never();
            e.printStackTrace();
        }

    }

    @Override
    public
    Match getNodeMatcher() {
        return null;
    }

    @Override
    public Match getWayMatcher() {
        return this.fenceMatcher;
    }

    @Override
    public Match getRelationMatcher() {
        return this.fenceRelationMatcher;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public List<Model> getModels() {
        return this.modelList;
    }

    @Override
    public void addModel(Node node, Perspective3D pPerspective3D) {
//        this.modelList.add(new Tree(node, pPerspective3D));
//        this.modelList.add(new Tree(node, pPerspective3D));

    }

    @Override
    public void addModel(Way way, Perspective3D pPerspective3D) {
        this.modelList.add(new Fence2(way, pPerspective3D));
    }

    @Override
    public void addModel(Relation relation, Perspective3D pPerspective3D) {
        this.modelList.add(new FenceRelation(relation, pPerspective3D));
    }

    @Override
    public void clear() {
        this.modelList.clear();
    }

}
