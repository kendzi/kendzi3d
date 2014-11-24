/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.building.builder.roof;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.building.builder.roof.registry.RoofTypeBuilderRegistry;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingUtil;
import kendzi.josm.kendzi3d.jogl.model.building.model.roof.RoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.DormerTypeBuilder;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofDebugOut;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureIndex;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofDormerTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementUnit;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType0v0;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofTypeBuilder;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

/**
 * Dormer roof builder.
 *
 * @author Tomasz Kędziora (kendzi)
 */
public class DormerRoofBuilder {
    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(DormerRoofBuilder.class);

    /**
     * Dormer roof builder.
     *
     * @param buildingPart
     *
     * @param height
     *            roof maximal height. Taken from building
     * @param mf
     * @param roofTextureData
     *            texture data
     * @return roof model
     */
    public static RoofOutput build(BuildingPart buildingPart, double height, ModelFactory mf, RoofTextureData roofTextureData) {

        RoofMaterials roofMaterials = addMaterials(roofTextureData, mf);

        RoofModel roofModel = buildingPart.getRoof();

        validate(roofModel);

        DormerRoofModel dormerRoof = (DormerRoofModel) roofModel;

        {
            // FIXME
            if (dormerRoof.getMeasurements().get(MeasurementKey.HEIGHT_1) == null && buildingPart.getRoofLevels() != null) {

                double roofHeight = buildingPart.getDefaultRoofHeight();
                if (buildingPart.getRoofLevels() < 1) {
                    roofHeight = 1d;
                }
                dormerRoof.getMeasurements().put(MeasurementKey.HEIGHT_1, new Measurement(roofHeight, MeasurementUnit.METERS));
            }
        }

        PolygonWithHolesList2d buildingPolygon = BuildingUtil.buildingPartToPolygonWithHoles(buildingPart);

        List<Point2d> polygon = buildingPolygon.getOuter().getPoints();
        // cleanPolygon(roof.getBuilding().getPoints());

        Point2d startPoint = polygon.get(0);

        RoofTypeBuilder roofType = getRoofType(dormerRoof);

        RoofTypeOutput rto = roofType.buildRoof(startPoint, buildingPolygon, dormerRoof, height, roofMaterials);

        List<RoofDormerTypeOutput> roofExtensionsList = DormerTypeBuilder.build(rto.getRoofHooksSpaces(), dormerRoof,
                dormerRoof.getMeasurements(), roofMaterials);

        double minHeight = height - rto.getHeight();

        buildModel(rto, roofExtensionsList, mf);

        RoofDebugOut debug = buildDebugInfo(rto, roofExtensionsList, startPoint, minHeight);

        RoofOutput out = new RoofOutput();
        out.setHeight(rto.getHeight());
        out.setDebug(debug);

        return out;
    }

    private static void validate(RoofModel roofModel) {
        if (!(roofModel instanceof DormerRoofModel)) {
            throw new IllegalArgumentException("wrong roof model, should be DormerRoofModel but it is: " + roofModel);
        }
    }

    private static RoofTypeBuilder getRoofType(DormerRoofModel roof) {

        DormerRoofModel dormerRoof = roof;

        RoofTypeBuilder roofType = RoofTypeBuilderRegistry.selectBuilder(dormerRoof.getRoofType());

        if (roofType == null) {
            roofType = new RoofType0v0();
        }
        return roofType;
    }

    private static RoofMaterials addMaterials(RoofTextureData pRoofTextureData, ModelFactory model) {
        RoofTextureIndex facadeTextureIndex = addMateraialTexture(pRoofTextureData.getFacadeTexture(),
                pRoofTextureData.getFacadeColor(), model);
        RoofTextureIndex roofTextureIndex = addMateraialTexture(pRoofTextureData.getRoofTexture(),
                pRoofTextureData.getRoofColor(), model);

        RoofMaterials roofMaterials = new RoofMaterials();
        roofMaterials.setFacade(facadeTextureIndex);
        roofMaterials.setRoof(roofTextureIndex);
        return roofMaterials;
    }

    /**
     * Add material representing texture to model.
     *
     * @param pTextureData
     * @param pModel
     * @return
     */
    private static RoofTextureIndex addMateraialTexture(TextureData pTextureData, Color pColor, ModelFactory pModel) {

        Material facadeMaterial = null;
        if (pColor != null) {
            facadeMaterial = MaterialFactory.createTextureColorMaterial(pTextureData.getTex0(), pColor);
        } else {
            facadeMaterial = MaterialFactory.createTextureMaterial(pTextureData.getTex0());
        }

        int facadeMaterialIndex = pModel.addMaterial(facadeMaterial);

        return new RoofTextureIndex(facadeMaterialIndex, pTextureData);
    }

    /**
     * Remove last point if it is the same as first.
     *
     * @param pRoofPolygon
     *            polygon
     * @return list of points
     */
    private static List<Point2d> cleanPolygon(List<Point2d> pRoofPolygon) {
        if (pRoofPolygon == null) {
            return null;
        }

        List<Point2d> ret = new ArrayList<Point2d>();
        for (Point2d point : pRoofPolygon) {
            ret.add(point);
        }

        int size = pRoofPolygon.size();

        if (size > 1 && pRoofPolygon.get(0).equals(pRoofPolygon.get(size - 1))) {
            ret.remove(size - 1);
        }

        return ret;
    }

    private static void transformMeshFactory(MeshFactory meshFactory, SimpleMatrix transformationMatrix) {

        MeshFactory mesh = meshFactory;

        SimpleMatrix normalMatrix = transformationMatrix.invert().transpose();

        Set<Vector3d> procesed = new HashSet<Vector3d>();

        List<Point3d> vertices = new ArrayList<Point3d>();
        for (int i = 0; i < mesh.vertices.size(); i++) {
            Point3d p = mesh.vertices.get(i);

            vertices.add(TransformationMatrix3d.transform(p, transformationMatrix));
        }
        mesh.vertices = vertices;

        List<Vector3d> normals = new ArrayList<Vector3d>();
        for (int i = 0; i < mesh.normals.size(); i++) {
            Vector3d v = mesh.normals.get(i);
            // if (procesed.contains(v)) {
            // continue;
            // }
            procesed.add(v);

            v = TransformationMatrix3d.transform(v, normalMatrix);
            // XXX !!!;
            v.normalize();

            normals.add(v);
        }
        mesh.normals = normals;
    }

    private static RoofDebugOut buildDebugInfo(RoofTypeOutput rto, List<RoofDormerTypeOutput> roofExtensionsList,
            Point2d startPoint, double height) {

        Point3d startPointMark = new Point3d(startPoint.x, height, -startPoint.y);

        List<Point3d> rectangleTransf = new ArrayList<Point3d>();

        List<Point3d> rectangle = rto.getRectangle();
        for (int i = 0; i < rectangle.size(); i++) {
            Point3d p = rectangle.get(i);

            rectangleTransf.add(TransformationMatrix3d.transform(p, rto.getTransformationMatrix()));
        }
        rto.setRectangle(rectangleTransf);
        RoofDebugOut out = new RoofDebugOut();
        out.setBbox(rectangleTransf);
        out.setStartPoint(startPointMark);
        return out;
    }

    private static void buildModel(RoofTypeOutput rto, List<RoofDormerTypeOutput> roofExtensionsList, ModelFactory modelFactory) {

        for (MeshFactory mf : rto.getMesh()) {
            transformMeshFactory(mf, rto.getTransformationMatrix());
            modelFactory.addMesh(mf);
        }

        for (RoofDormerTypeOutput e : roofExtensionsList) {
            if (e == null) {
                continue;
            }

            SimpleMatrix roofMatrix = rto.getTransformationMatrix().mult(e.getTransformationMatrix());

            for (MeshFactory mf : e.getMesh()) {

                transformMeshFactory(mf, roofMatrix);

                modelFactory.addMesh(mf);
            }
        }

    }
}
