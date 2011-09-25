/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Material;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.josm.kendzi3d.jogl.model.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.skeleton.Skeleton;
import kendzi.math.geometry.skeleton.Skeleton.Output;

import org.apache.log4j.Logger;
import org.ejml.data.SimpleMatrix;

/**
 * Roof type 9.0.
 *
 * @author Tomasz Kêdziora (Kendzi)
 *
 */
public class RoofType9_0 implements RoofType {

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType9_0.class);

    @Override
    public String getPrefixKey() {
        return "9.0";
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRoof(Point2d pStartPoint, List<Point2d> border, Integer prefixParameter, double height,
            Map<MeasurementKey, Measurement> pMeasurements, RoofTextureData pRoofTextureData) {

        SimpleMatrix transformLocal = TransformationMatrix2d.tranA(-pStartPoint.x, -pStartPoint.y);

        border = TransformationMatrix2d.transformList(border, transformLocal);

        // rectangleContur = TransformationMatrix2d.transformArray(rectangleContur, transformLocal);

        RoofTypeOutput rto = build(border, height, 0, 0, pRoofTextureData);

        SimpleMatrix transformGlobal = TransformationMatrix3d.tranA(pStartPoint.x, height - rto.getHeight(),
                -pStartPoint.y);
        rto.setTransformationMatrix(transformGlobal);

        return rto;

    }

    protected RoofTypeOutput build(List<Point2d> pBorderList,

    double h1, double h2, double l2, RoofTextureData pRoofTextureData) {

        ModelFactory model = ModelFactory.modelBuilder();
        MeshFactory meshBorder = model.addMesh("roof_border");
        MeshFactory meshRoof = model.addMesh("roof_top");

        // XXX move it
        TextureData facadeTexture = pRoofTextureData.getFacadeTextrure();
        TextureData roofTexture = pRoofTextureData.getRoofTexture();
        Material facadeMaterial = MaterialFactory.createTextureMaterial(facadeTexture.getFile());
        Material roofMaterial = MaterialFactory.createTextureMaterial(roofTexture.getFile());
        // XXX move material
        int facadeMaterialIndex = model.addMaterial(facadeMaterial);
        int roofMaterialIndex = model.addMaterial(roofMaterial);

        meshBorder.materialID = facadeMaterialIndex;
        meshBorder.hasTexture = true;

        meshRoof.materialID = roofMaterialIndex;
        meshRoof.hasTexture = true;



        Output sk = Skeleton.sk(pBorderList);

        for (PolygonList2d polygon : sk.faces2) {
            List<Point2d> points = polygon.getPoints();

            if (points.size() < 3) {
                log.error("blad za malo wiezcholkow !!!!!!");
                continue;
            }
            int s1 = points.size();

            List<Point2d> cleanPointList = Triangulate.removeClosePoints(points);
            List<Point2d> trianglePolygon = Triangulate.process(cleanPointList);

            FaceFactory face = meshRoof.addFace(FaceType.TRIANGLES);
            int normalIndex = meshRoof.addNormal(new Vector3d(0,1,0));
            int cordIndex = meshRoof.addTextCoord(new TextCoord(0,0));
            for (Point2d p : trianglePolygon) {

                double h = sk.distance.get(p);

                int vi = meshRoof.addVertex(new Point3d(p.x, h, -p.y));

                face.addVert(vi, cordIndex, normalIndex);


            }

        }

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(Math.max(h1, h2));

        rto.setModel(model);

        // RoofHooksSpace [] rhs =
        // buildRectRoofHooksSpace(
        // pRectangleContur,
        // new PolygonPlane(bottomMP, planeBottom),
        // null,
        // new PolygonPlane(topMP, planeTop),
        // null
        // );

        // rto.setRoofHooksSpaces(rhs);


        rto.setRectangle(findRectangle(pBorderList, 0));
        return rto;

    }

    /** Find minimal rectangle containing list of points.
     * Save as list of 3d points to display.
     *
     * XXX this should by changed!
     *
     * @param pPolygon list of points
     * @param height height
     * @return minimal rectangle
     */
    protected List<Point3d> findRectangle(List<Point2d> pPolygon, double height) {

        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;

        for (Point2d p : pPolygon) {
            if (minx > p.x) {
                minx = p.x;
            }
            if (miny > p.y) {
                miny = p.y;
            }
            if (maxx < p.x) {
                maxx = p.x;
            }
            if (maxy < p.y) {
                maxy = p.y;
            }
        }


        List<Point3d> rect = new ArrayList<Point3d>();
        rect.add(new Point3d(minx, height, -miny));
        rect.add(new Point3d(minx, height, -maxy));
        rect.add(new Point3d(maxx, height, -maxy));
        rect.add(new Point3d(maxx, height, -miny));

        return rect;
    }

}
