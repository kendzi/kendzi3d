package kendzi.josm.kendzi3d.jogl.model.clone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kendzi.josm.kendzi3d.jogl.ModelUtil;
import kendzi.josm.kendzi3d.jogl.model.attribute.OsmAttributeKeys;
import kendzi.josm.kendzi3d.jogl.model.attribute.OsmAttributeValues;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;

public class RelationCloneLevel implements java.lang.Iterable<Double> {

    private double offset = 0;

    private int from = 1;

    private int repeat = 0;

    private int every = 1;

    private double levelHeight = 2.5d;

    private Integer to = 1;
//    private double offset =0;

    public static List<RelationCloneLevel> buildHeightClone(OsmPrimitive pOsmPrimitive, double pLevelHeight) {
        List<RelationCloneLevel> ret = new ArrayList<RelationCloneLevel>();

        if (pOsmPrimitive == null) {
            return ret;
        }

        for (OsmPrimitive op : pOsmPrimitive.getReferrers()) {
            if (op instanceof Relation) {
                Relation r = (Relation) op;

                if (OsmAttributeValues.CLONE_LEVEL.equals(OsmAttributeKeys.TYPE.primitiveValue(op))) {
                    RelationCloneLevel clone2 = parseHeightClone(r, pLevelHeight);

                    if (clone2 != null) {
                        ret.add(clone2);
                    }
                }
            }
        }

        return ret;
    }

    private static RelationCloneLevel parseHeightClone(Relation pRelation, double pLevelHeight) {
        if (pRelation == null) {
            return null;
        }

        RelationCloneLevel clone = new RelationCloneLevel();
        clone.setOffset(
                ModelUtil.parseHeight(pRelation.get("offset"), 0d));

        clone.setRepeat(parseInt(pRelation.get("repeat"), 0));

        clone.setEvery(parseInt(pRelation.get("every"), 0));

        clone.setFrom(parseInt(pRelation.get("from"), 0));

        clone.setTo(parseInt(pRelation.get("to"), 0));

        clone.setLevelHeight(pLevelHeight);

        return clone;

    }

    static Integer parseInt(String pStr, Integer pDef) {
        try {
            return Integer.parseInt(pStr);
        } catch (Exception e) {
            //
        }
        return pDef;
    }

    /**
     * @return the offset
     */
    public double getOffset() {
        return this.offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(double offset) {
        this.offset = offset;
    }

    /**
     * @return the repeat
     */
    public int getRepeat() {
        return this.repeat;
    }

    /**
     * @param repeat the repeat to set
     */
    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    /**
     * @return the every
     */
    public int getEvery() {
        return this.every;
    }

    /**
     * @param every the every to set
     */
    public void setEvery(int every) {
        this.every = every;
    }

    @Override
    public Iterator<Double> iterator() {
        Iterator<Double> ret = new Iterator<Double>() {

            int loop = RelationCloneLevel.this.from;

            @Override
            public boolean hasNext() {
                if (to != null) {
                    return this.loop < to;
                }
                return this.loop < RelationCloneLevel.this.repeat;
            }

            @Override
            public Double next() {
                if (!hasNext()) {
                    return null;
                }
                this.loop++;
                return this.loop * (RelationCloneLevel.this.every * levelHeight) + RelationCloneLevel.this.offset ;
            }

            @Override
            public void remove() {
                throw new RuntimeException("method remove is not allowed");
            }

        };

        return ret;
    }

    /**
     * @return the from
     */
    public int getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(int from) {
        this.from = from;
    }

    /**
     * @return the levelHeight
     */
    public double getLevelHeight() {
        return levelHeight;
    }

    /**
     * @param levelHeight the levelHeight to set
     */
    public void setLevelHeight(double levelHeight) {
        this.levelHeight = levelHeight;
    }

    /**
     * @return the to
     */
    public Integer getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(Integer to) {
        this.to = to;
    }



}
