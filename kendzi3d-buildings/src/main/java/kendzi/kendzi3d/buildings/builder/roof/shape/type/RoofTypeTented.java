/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.math.geometry.Graham;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonUtil;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

/**
 * Roof type Tented.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofTypeTented extends RectangleRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1,
                conf.getRoofTextureData());

    }

    /**
     * @param buildingPolygon
     *            the building polygon
     * @param recHeight
     *            the bonding rectangle height
     * @param recWidth
     *            the bonding rectangle width
     * @param rectangleContur
     *            the bonding rectangle
     * @param h1
     *            the roof height
     * @param roofTextureData
     *            the roof texture data
     * @return the roof output
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double recHeight, double recWidth,
            Point2d[] rectangleContur, double h1, RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        List<Point2d> outlineList = buildingPolygon.getOuter().getPoints();

        if (PolygonUtil.isClockwisePolygon(outlineList)) {
            outlineList = PolygonList2d.reverse(outlineList);
        }

        List<Point2d> outlineConvexHull = Graham.grahamScan(outlineList);

        Point2d middlePoint = RoofTypePyramidal.findMiddlePoint(outlineConvexHull);

        MultiPolygonList2d[] mp = createMP(outlineList, middlePoint);

        Plane3d[] planes = RoofTypePyramidal.createPlanes(outlineList, h1, middlePoint);

        Vector3d[] roofLine = RoofTypePyramidal.createRoofLines(outlineList);

        double[] textureOffset = RoofTypePyramidal.createTextureOffset(outlineList);

        for (int i = 0; i < mp.length; i++) {

            MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mp[i], planes[i], roofLine[i], roofTexture, textureOffset[i],
                    0);
        }

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1);

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        return rto;
    }

    /**
     * Create roof surface polygons.
     * 
     * @param outlinePolygon
     * @param middlePoint
     * @return
     */
    private MultiPolygonList2d[] createMP(List<Point2d> outlinePolygon, Point2d middlePoint) {

        int size = outlinePolygon.size();

        MultiPolygonList2d[] ret = new MultiPolygonList2d[size];

        for (int i = 0; i < size; i++) {
            Point2d p1 = outlinePolygon.get(i);
            Point2d p2 = outlinePolygon.get((i + 1) % size);

            ret[i] = new MultiPolygonList2d(new PolygonList2d(p1, p2, middlePoint));
        }

        return ret;
    }
}
