/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.util.EnumMap;

import javax.media.opengl.GL2;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.ModelCache;
import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Bounds;
import kendzi.jogl.model.geometry.Material;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.ModelUtil;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractPointModel;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import net.java.joglutils.model.ModelLoadException;

import org.openstreetmap.josm.data.osm.Node;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

public class Tree extends AbstractPointModel implements DLODSuport {


    private Texture treeText;
    float[] verts;

    private Model model;
    private ModelRender modelRenderer;
    private double modelScaleHeight;
    private double modelScaleWidht;
    private EnumMap<LOD, Model> modelLod;
    private String type;
    private String genus;
    private String species;
    private boolean isFlat;
    Vector3d scale;

    public Tree(Node node, Perspective3D pPerspective3D) {
        super(node, pPerspective3D);



        this.modelLod = new EnumMap<LOD, Model>(LOD.class);

        this.scale = new Vector3d(1d, 1d, 1d);

        this.modelRenderer = ModelRender.getInstance();


    }

    @Override
    public void buildModel() {


//        int lod = 3;
//
//        double treeHeight = ModelUtil.getHeight(this.node, 10d);
//
//
//        float treeWidth = (float) (treeHeight / 2.0);
//
//        // tree model
//        float[] verts = { -treeWidth / 2, 0, 0, treeWidth / 2, 0, 0, treeWidth / 2, (float) treeHeight, 0,
//                -treeWidth / 2, (float) treeHeight, 0 };
//
//
//        this.verts = verts;
//
//        this.treeText = TextureCacheService.getTextureFromDir("tree_unknown.png");
//        if ("broad_leafed".equals(this.node.get("type"))) {
//            this.treeText = TextureCacheService.getTexture("tree_broad_leafed.png");
//        } else if ("conifer".equals(this.node.get("type"))) {
//            this.treeText = TextureCacheService.getTexture("tree_conifer.png");
//        }
//
        this.type = this.node.get("type");
        if (this.type == null) {
            this.type = "unknown";
        }
        this.genus = this.node.get("genus");
        this.species = this.node.get("species");
//
//        //        this.x = this.perspective.calcX(this.node.getEastNorth().getX());
//        //        this.y = this.perspective.calcY(this.node.getEastNorth().getY());
//
//

//
//
//        try {
////                        this.model2 = ModelCache.loadModel("models/obj/penguin.obj");
////            this.model2 = ModelCache.loadModel("models/obj/tree1.obj");
//            this.model = ModelCache.loadModel("models/obj/tree3.obj");
////            this.model2 = ModelCache.loadModel("models/obj/box.obj");
//        } catch (ModelLoadException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        // When loading the model, adjust the center to the boundary center
//        //        this.model2.centerModelOnPosition(true);
//        this.model.useLigth = true;
////        this.model.setUseTexture(true);
////        this.model.drawNormals = true;
////        this.model.drawEdges = true;
//
//        setAmbientColor(this.model);
//
//        // Render the bounding box of the entire model
//        //        this.model2.setRenderModelBounds(false);
//
//        // Render the bounding boxes for all of the objects of the model
//        //        this.model2.setRenderObjectBounds(false);
//
//        // Make the model unit size
//        //        this.model2.setUnitizeSize(true);
//
//        // Get the radius of the model to use for lighting and view presetting
//        double radius = this.model.getBounds().radius;
//
//
//        Bounds bounds = this.model.getBounds();
//
//        double modelHeight = bounds.max.y;
//        double modelWidht = Math.max(bounds.max.x - bounds.min.x, bounds.max.z - bounds.min.z);
//
//        this.modelScaleHeight = treeHeight / modelHeight;
//
//        this.modelScaleWidht = this.modelScaleHeight;


        // Get an instance of the display list renderer a renderer
        // this.modelRenderer = DisplayListRenderer.getInstance();
        this.modelRenderer = ModelRender.getInstance();
        // Turn on debugging
        // this.modelRenderer.debug(true);

        buildModel(LOD.LOD1);

        this.buildModel = true;
    }

    @Override
    public void buildModel(LOD pLod) {

        this.type = this.node.get("type");
        if (this.type == null) {
            this.type = "unknown";
        }
        this.genus = this.node.get("genus");
        this.species = this.node.get("species");

        double height = getHeight(this.node);

        Model model = null;

//        switch (pLod) {
//        case LOD1:
//        case LOD2:
//            model = buildFlatModel();
//            break;
//        case LOD3:
//        case LOD4:
//        case LOD5:
//
//
//
//            model = findSimpleModel(LOD.LOD3);
//
//
//            break;
//
//
//
//        default:
//
//        }

        model = findSimpleModel(pLod);

        setupScale(model, height);

//        if (LOD.LOD3.equals(pLod)) {
//
//            MetadataCacheService.getModel("models/obj/broad_leafed")
//            MetadataCacheService.getModel("models/obj/tree3.obj")
//
//        }


        this.modelLod.put(pLod, model);
    }

    private void setupScale(Model model2, double height) {

        Bounds bounds = model2.getBounds();

        double modelHeight = bounds.max.y;
        double modelWidht = Math.max(bounds.max.x - bounds.min.x, bounds.max.z - bounds.min.z);

        double modelScaleHeight = height / modelHeight;

        double modelScaleWidht = modelScaleHeight;

        this.scale.x = modelScaleWidht;
        this.scale.y = modelScaleHeight;
        this.scale.z = modelScaleWidht;

//        model2.useScale = true;
    }

    /**
     * Finds simple model for tree. Order of finding is:
     * - species
     * - genus
     * - type
     *
     *
     * @return
     */
    private Model findSimpleModel(LOD pLod) {
        // let go
        String model = null;

        String spacesModel = MetadataCacheService.getPropertites("models.trees.species.{0}.{1}.model", null, this.species, "" + pLod);
        String genusModel = MetadataCacheService.getPropertites("models.trees.genus.{0}.{1}.model", null, this.genus, "" + pLod);
        String typeModel = MetadataCacheService.getPropertites("models.trees.type.{0}.{1}.model", null, this.type, "" + pLod);

        // XXX add StringUtil
        if (spacesModel != null) {
            model = spacesModel;
        } else if (genusModel != null) {
            model = genusModel;
        } else if (typeModel != null) {
            model = typeModel;
        }

        if (model != null && !"#flat".equals(model)) {
            try {
                Model loadModel = ModelCache.loadModel(model);
                loadModel.useLight = true;
                setAmbientColor(loadModel);
                return loadModel;

            } catch (ModelLoadException e) {
                // XXX
                e.printStackTrace();
            }
        } else {
            //"#flat"
            // XXX
            this.isFlat = true;
            return buildFlatModel();
        }

        return null;

    }

    private String unknown(String pStr) {
        if (pStr == null) {
            return "unknown";
        }
        return pStr;
    }

    /**
     * Finds height for tree. Order of finding is:
     * - node attribute
     * - species
     * - genus
     * - type
     *
     *
     * @return
     */
    private double getHeight(Node node) {
        // let go

        Double height = 1d;

        Double nodeHeight = ModelUtil.getHeight(node, null);

        Double spacesHeight = MetadataCacheService.getPropertitesDouble("models.trees.species." + this.species + ".height", null);
        Double genusHeight = MetadataCacheService.getPropertitesDouble("models.trees.genus." + this.genus + ".height", null);
        Double typeHeight = MetadataCacheService.getPropertitesDouble("models.trees.type." + unknown(this.type) + ".height", null);
         MetadataCacheService.getPropertitesDouble("models.trees." + this.type + ".height", null);

        // XXX add StringUtil
        if (nodeHeight != null) {
            height = nodeHeight;
        } else if (spacesHeight != null) {
            height = spacesHeight;
        } else if (genusHeight != null) {
            height = genusHeight;
        } else if (typeHeight != null) {
            height = typeHeight;
        }

        return height;
    }

    private Model buildFlatModel() {

        Model model = new FlatModel();

//        model.mesh = new Mesh();

        ModelFactory mf = ModelFactory.modelBuilder();
        Material mat = new Material();
        mat.strName = "flat_texture";
        mat.strFile = getFlatTextureFile();

        int mi = mf.addMaterial(mat);

        MeshFactory mesh = mf.addMesh("flat");
        mesh.materialID = mi;
        mesh.hasTexture = true;

        int p1 = mesh.addVertex(new Point3d(-0.25d, 0, 0));
        int p2 = mesh.addVertex(new Point3d(0.25d, 0, 0));
        int p3 = mesh.addVertex(new Point3d(0.25d, 1, 0));
        int p4 = mesh.addVertex(new Point3d(-0.25d, 1, 0));

        int uv1 = mesh.addTextCoord(new TextCoord(0, 0));
        int uv2 = mesh.addTextCoord(new TextCoord(1, 0));
        int uv3 = mesh.addTextCoord(new TextCoord(1, 1));
        int uv4 = mesh.addTextCoord(new TextCoord(0, 1));

        FaceFactory face = mesh.addFace(FaceType.QUADS);

        face.addVertIndex(p1);
        face.addVertIndex(p2);
        face.addVertIndex(p3);
        face.addVertIndex(p4);

        face.addCoordIndex(uv1);
        face.addCoordIndex(uv2);
        face.addCoordIndex(uv3);
        face.addCoordIndex(uv4);

        Model model2 = mf.toModel();

        model.setBounds(model2.getBounds());
        model.mesh = model2.mesh;
        model.materials = model2.materials;
        model.setUseTexture(true);
        model.useLight = false;

        return model;



//        double treeHeight = 1d; //ModelUtil.getHeight(this.node, 10d);
//
//        float treeWidth = 1f;// (float) (treeHeight / 2.0);
//
//        // tree model
//        float[] verts = {
//                -treeWidth / 2, 0, 0,
//                treeWidth / 2, 0, 0,
//                treeWidth / 2, (float) treeHeight, 0,
//                -treeWidth / 2, (float) treeHeight, 0 };
//
//
//        this.verts = verts;
//
//        this.treeText = TextureCacheService.getTextureFromDir("tree_unknown.png");
//        if ("broad_leafed".equals(this.node.get("type"))) {
//            this.treeText = TextureCacheService.getTexture("tree_broad_leafed.png");
//        } else if ("conifer".equals(this.node.get("type"))) {
//            this.treeText = TextureCacheService.getTexture("tree_conifer.png");
//        }
    }

    private String getFlatTextureFile() {
        //FIXME !!! add cache !!!
        //FIXME add model builder !!!

        String textFile = "/textures/tree_unknown.png";

        String spacesText = MetadataCacheService.getPropertites("models.trees.species." + this.species + ".flat.texture", null);
        String genusText = MetadataCacheService.getPropertites("models.trees.genus." + this.genus + ".flat.texture", null);
        String typeText = MetadataCacheService.getPropertites("models.trees.type." + this.type + ".flat.texture", null);

        // XXX add StringUtil
        if (spacesText != null) {
            textFile = spacesText;
        } else if (genusText != null) {
            textFile = genusText;
        } else if (typeText != null) {
            textFile = typeText;
        }



//        String textFile = "/textures/tree_unknown.png";
//        if ("broad_leafed".equals(this.node.get("type"))) {
//            textFile = "/textures/tree_broad_leafed.png";
//        } else if ("conifer".equals(this.node.get("type"))) {
//            textFile = "/textures/tree_conifer.png";
//        }
//
        return textFile;
    }

    class FlatModel extends Model {

    }




    @Override
    public boolean isModelBuild(LOD pLod) {

        if (this.modelLod.get(pLod) != null) {
            return true;
        }
        return false;
    }

    private void setAmbientColor(Model model22) {
        for (int i = 0; i < model22.getNumberOfMaterials(); i++) {
            Material material = model22.getMaterial(i);
//            material.diffuseColor
            material.ambientColor = material.diffuseColor;
//                new Color(102, 102, 102);
        }

    }

    @Override
    public void draw(GL2 gl, Camera camera, LOD pLod) {
        Model model2 = this.modelLod.get(pLod);
        if (model2 != null) {

            gl.glPushMatrix();
            gl.glTranslated(this.getGlobalX(), 0, -this.getGlobalY());

            gl.glEnable(GL2.GL_NORMALIZE);
            gl.glScaled(this.scale.x, this.scale.y, this.scale.z);

            if (model2 instanceof FlatModel) {

                gl.glRotatef(-1 * ((float) Math.toDegrees(-camera.getAngle().y) + 90.0f), 0, 1, 0);

//                this.modelRenderer.render(gl, model2);
                drawFlatTree(gl, model2);

            } else {
                this.modelRenderer.render(gl, model2);

            }
            gl.glDisable(GL2.GL_NORMALIZE);




            // rotate in the opposite direction to the camera

            gl.glPopMatrix();

        }
    }


    @Override
    public void draw(GL2 gl, Camera camera) {
        draw(gl, camera, LOD.LOD1);
    }

    /*
     * A screen is a transparent quadrilateral which only shows the
     * non-transparent parts of the texture. Lighting is disabled. The screen is
     * positioned according to the vertices in verts[].
     */
    private void drawFlatTree(GL2 gl, Model model2) {
        boolean enableLightsAtEnd = false;
        if (gl.glIsEnabled(GL2.GL_LIGHTING)) { // switch lights off if currently
            // on
            gl.glDisable(GL2.GL_LIGHTING);
            enableLightsAtEnd = true;
        }

        // do not draw the transparent parts of the texture
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        // don't show source alpha parts in the destination

        // determine which areas of the polygon are to be rendered
        gl.glEnable(GL2.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL2.GL_GREATER, 0); // only render if alpha > 0


        // enable texturing
        gl.glEnable(GL2.GL_TEXTURE_2D);
//        tex.bind();


        // replace the quad colours with the texture
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);

        this.modelRenderer.render(gl, model2);

//        TextureCoords tc = tex.getImageTexCoords();
//
//        gl.glBegin(GL2.GL_QUADS);
//        gl.glTexCoord2f(tc.left(), tc.bottom());
//        gl.glVertex3f(verts[0], verts[1], verts[2]);
//
//        gl.glTexCoord2f(tc.right(), tc.bottom());
//        gl.glVertex3f(verts[3], verts[4], verts[5]);
//
//        gl.glTexCoord2f(tc.right(), tc.top());
//        gl.glVertex3f(verts[6], verts[7], verts[8]);
//
//        gl.glTexCoord2f(tc.left(), tc.top());
//        gl.glVertex3f(verts[9], verts[10], verts[11]);
//        gl.glEnd();

        gl.glDisable(GL2.GL_TEXTURE_2D);

        // switch back to modulation of quad colours and texture
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
        gl.glDisable(GL2.GL_ALPHA); // switch off transparency
        gl.glDisable(GL2.GL_BLEND);

        if (enableLightsAtEnd) {
            gl.glEnable(GL2.GL_LIGHTING);
        }
    } // end of drawScreen()
    /*
     * A screen is a transparent quadrilateral which only shows the
     * non-transparent parts of the texture. Lighting is disabled. The screen is
     * positioned according to the vertices in verts[].
     */
    private void drawFlatTree(GL2 gl, float[] verts, Texture tex) {
        boolean enableLightsAtEnd = false;
        if (gl.glIsEnabled(GL2.GL_LIGHTING)) { // switch lights off if currently
            // on
            gl.glDisable(GL2.GL_LIGHTING);
            enableLightsAtEnd = true;
        }

        // do not draw the transparent parts of the texture
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        // don't show source alpha parts in the destination

        // determine which areas of the polygon are to be rendered
        gl.glEnable(GL2.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL2.GL_GREATER, 0); // only render if alpha > 0


        // enable texturing
        gl.glEnable(GL2.GL_TEXTURE_2D);
        tex.bind();

        // replace the quad colours with the texture
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);

        TextureCoords tc = tex.getImageTexCoords();

        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(tc.left(), tc.bottom());
        gl.glVertex3f(verts[0], verts[1], verts[2]);

        gl.glTexCoord2f(tc.right(), tc.bottom());
        gl.glVertex3f(verts[3], verts[4], verts[5]);

        gl.glTexCoord2f(tc.right(), tc.top());
        gl.glVertex3f(verts[6], verts[7], verts[8]);

        gl.glTexCoord2f(tc.left(), tc.top());
        gl.glVertex3f(verts[9], verts[10], verts[11]);
        gl.glEnd();

        gl.glDisable(GL2.GL_TEXTURE_2D);

        // switch back to modulation of quad colours and texture
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
        gl.glDisable(GL2.GL_ALPHA); // switch off transparency
        gl.glDisable(GL2.GL_BLEND);

        if (enableLightsAtEnd) {
            gl.glEnable(GL2.GL_LIGHTING);
        }
    } // end of drawScreen()

    @Override
    public Point3d getPoint() {
        return new Point3d(this.x, 0, -this.y);
    }


}
