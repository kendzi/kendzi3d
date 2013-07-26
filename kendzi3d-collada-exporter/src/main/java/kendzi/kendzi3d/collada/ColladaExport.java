package kendzi.kendzi3d.collada;

import java.awt.Color;
import java.io.File;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.util.index.SimplifyIndexArray;

import org.collada._2005._11.colladaschema.Accessor;
import org.collada._2005._11.colladaschema.Asset;
import org.collada._2005._11.colladaschema.Asset.Contributor;
import org.collada._2005._11.colladaschema.BindMaterial;
import org.collada._2005._11.colladaschema.COLLADA;
import org.collada._2005._11.colladaschema.COLLADA.Scene;
import org.collada._2005._11.colladaschema.CommonColorOrTextureType;
import org.collada._2005._11.colladaschema.CommonColorOrTextureType.Texture;
import org.collada._2005._11.colladaschema.CommonNewparamType;
import org.collada._2005._11.colladaschema.Effect;
import org.collada._2005._11.colladaschema.FloatArray;
import org.collada._2005._11.colladaschema.FxSampler2DCommon;
import org.collada._2005._11.colladaschema.FxSurfaceCommon;
import org.collada._2005._11.colladaschema.FxSurfaceInitFromCommon;
import org.collada._2005._11.colladaschema.Geometry;
import org.collada._2005._11.colladaschema.Image;
import org.collada._2005._11.colladaschema.InputLocal;
import org.collada._2005._11.colladaschema.InputLocalOffset;
import org.collada._2005._11.colladaschema.InstanceEffect;
import org.collada._2005._11.colladaschema.InstanceGeometry;
import org.collada._2005._11.colladaschema.InstanceMaterial;
import org.collada._2005._11.colladaschema.InstanceMaterial.BindVertexInput;
import org.collada._2005._11.colladaschema.InstanceWithExtra;
import org.collada._2005._11.colladaschema.LibraryEffects;
import org.collada._2005._11.colladaschema.LibraryGeometries;
import org.collada._2005._11.colladaschema.LibraryImages;
import org.collada._2005._11.colladaschema.LibraryMaterials;
import org.collada._2005._11.colladaschema.LibraryVisualScenes;
import org.collada._2005._11.colladaschema.Node;
import org.collada._2005._11.colladaschema.ObjectFactory;
import org.collada._2005._11.colladaschema.Param;
import org.collada._2005._11.colladaschema.ProfileCOMMON;
import org.collada._2005._11.colladaschema.ProfileCOMMON.Technique;
import org.collada._2005._11.colladaschema.ProfileCOMMON.Technique.Phong;
import org.collada._2005._11.colladaschema.Source;
import org.collada._2005._11.colladaschema.Source.TechniqueCommon;
import org.collada._2005._11.colladaschema.Triangles;
import org.collada._2005._11.colladaschema.UpAxisType;
import org.collada._2005._11.colladaschema.Vertices;
import org.collada._2005._11.colladaschema.VisualScene;

public class ColladaExport extends TextExport {

    private Map<Material, String> matCatche = new HashMap<Material, String>();
    private Map<String, String> textureKeys = new HashMap<String, String>();


    private COLLADA c;

    private int id = 0;

    public ColladaExport() {
        init();
    }



    private void init() {
        this.c = createCollada();
    }

    @Override
    public Map<String, String> getTextureKeys() {
        return this.textureKeys;
    }


    @Override
    public void addModel(Model m) throws Exception {

        LibraryGeometries lg = getOrCreateLibraryGeometries();

        VisualScene vs = getOrCreateVisualScene();

        Node node = new Node();
        node.setName(name(getModelName(m.getSource())));

        vs.getNodes().add(node);


        for (Mesh mesh : m.mesh) {

            Map<Material, String> mats = new HashMap<Material, String>();

            //        String materialId = null;
            for (Material mat : m.materials) {
                String materialId = convert(this.c, mat);
                mats.put(mat, materialId);
            }

            for (int f = 0; f < mesh.face.length; f++) {
                Geometry geometry = new Geometry();
                geometry.setId(getId("geom"));
                geometry.setName(name(mesh.name));



                Face face = mesh.face[f];
                Material faceMaterial = m.getMaterial(mesh.materialID);

                addFace(m, node, mesh, geometry, mats,
                        face, faceMaterial);

                lg.getGeometries().add(geometry);
            }
        }
    }

    private String name(String name) {
        if (name == null) {
            return null;
        }

        name = name.replaceAll(" ", "_");
        name = name.replaceAll(":", "-");
        name = name.replaceAll("&", "-");
        name = name.replaceAll("\\+", "-");
        name = name.replaceAll(",", "-");
        name = name.replaceAll(";", "-");
        name = name.replaceAll("/", "-");

        name = name.replaceAll("^[0-9]", "n-");
        name = name.replaceAll("^[.]", "n-");
        name = name.replaceAll("^[-]", "n-");

        return name;
    }



    /**
     * @param m
     * @param node
     * @param mesh
     * @param geometry
     * @param mats
     * @param face
     * @param faceMaterial
     * @param vertexSource
     * @param normalsSource
     * @param texSource
     */
    public void addFace(Model m, Node node, Mesh mesh, Geometry geometry,
            Map<Material, String> mats,
            Face face ,
            Material faceMaterial
            ) {

        SimplifyIndexArray<Point3d> simpVertex =
                SimplifyIndexArray.simple(mesh.vertices, face.vertIndex, Point3d.class);

        SimplifyIndexArray<Vector3d> simpNormal =
                SimplifyIndexArray.simple(mesh.normals, face.normalIndex, Vector3d.class);

        SimplifyIndexArray<TextCoord> simpTex0 =
                SimplifyIndexArray.simple(mesh.texCoords, face.coordIndexLayers[0], TextCoord.class);

        Source vertexSource = createVertexSource(simpVertex.getSdata());
        Source normalsSource = createNormalSource(simpNormal.getSdata());
        Source tex0Source = createTexSource(simpTex0.getSdata());

        org.collada._2005._11.colladaschema.Mesh cmesh = new org.collada._2005._11.colladaschema.Mesh();
        geometry.setMesh(cmesh);

        String materialId = mats.get(faceMaterial);
        String materialSymbolId = createInstanceGeometry(node, geometry, materialId);

        cmesh.getSources().add(vertexSource);
        cmesh.getSources().add(normalsSource);
        cmesh.getSources().add(tex0Source);


        int numOfLayers = 0;
        if (face.coordIndexLayers != null) {
            numOfLayers = face.coordIndexLayers.length;
        }
        numOfLayers = 1;

        String [] texSourcesIds = new String[numOfLayers];
        texSourcesIds[0] = tex0Source.getId();

        @SuppressWarnings("unchecked")
        SimplifyIndexArray<TextCoord> texSimpleIndex[] = new SimplifyIndexArray[numOfLayers];
        texSimpleIndex[0] = simpTex0;

        for (int l = 1; l < numOfLayers; l++) {

            SimplifyIndexArray<TextCoord> simpTexN =
                    SimplifyIndexArray.simple(mesh.texCoords, face.coordIndexLayers[l], TextCoord.class);

            Source layerUVSource = createTexSource(simpTexN.getSdata());
            layerUVSource.setId(layerUVSource.getId() + "_" + l);
            cmesh.getSources().add(layerUVSource);

            texSourcesIds[l] =layerUVSource.getId();
            texSimpleIndex[l] = simpTexN;
        }

        InputLocal il = new InputLocal();
        il.setSemantic("POSITION");
        il.setSource("#" + vertexSource.getId());

        String vertecesId = getId("vertices");
        Vertices vertices = new Vertices();
        vertices.setId(vertecesId);
        vertices.getInputs().add(il);

        cmesh.setVertices(vertices);

        Triangles tri = new Triangles();

        InputLocalOffset inV = new InputLocalOffset();
        inV.setSemantic("VERTEX");
        inV.setSource("#" + vertices.getId());
        inV.setOffset(number(0));
        tri.getInputs().add(inV);

        InputLocalOffset inN = new InputLocalOffset();
        inN.setSemantic("NORMAL");
        inN.setSource("#" + normalsSource.getId());
        inN.setOffset(number(1));
        tri.getInputs().add(inN);



        for (int l = 0; l < numOfLayers; l++) {
            InputLocalOffset inC = new InputLocalOffset();
            inC.setSemantic("TEXCOORD");
            inC.setSource("#" + texSourcesIds[l]);//texSource.getId());
            inC.setOffset(number(2 + l));
            inC.setSet(number(l));
            tri.getInputs().add(inC);
        }

        List<BigInteger> triVert = convertToTriangles(simpVertex.getSindex(), face.type);
        List<BigInteger> triNorm = convertToTriangles(simpNormal.getSindex(), face.type);
        List<List<BigInteger>> triTcLayers = new ArrayList<List<BigInteger>>(numOfLayers);
        for (int l = 0; l < numOfLayers; l++) {
            List<BigInteger> triTc = convertToTriangles(texSimpleIndex[l].getSindex(), face.type);
            triTcLayers.add(triTc);
        }

        for (int i = 0; i < triVert.size(); i++) {
            tri.getP().add(triVert.get(i));
            tri.getP().add(triNorm.get(i));
            for (int l = 0; l < numOfLayers; l++) {
                tri.getP().add(triTcLayers.get(l).get(i));
            }
        }

        if (triVert.size() % 3 != 0) {
            throw new RuntimeException("bad number of vertex in triangle it should be always mult of 3: " + triVert.size());
        }

        tri.setCount(number(triVert.size() / 3));
        tri.setMaterial(materialSymbolId);
        cmesh.getLinesAndLinestripsAndPolygons().add(tri);

        //linesAndLinestripsAndPolygons
    }

    /**
     * @param node
     * @param geometry
     * @param materialId
     * @return
     */
    public String createInstanceGeometry(Node node, Geometry geometry,
            String materialId) {
        String materialSymbolId;
        // instance_geometry
        InstanceGeometry ig = new InstanceGeometry();
        ig.setUrl("#" + geometry.getId());

        BindMaterial bm = new BindMaterial();
        org.collada._2005._11.colladaschema.BindMaterial.TechniqueCommon tc = new org.collada._2005._11.colladaschema.BindMaterial.TechniqueCommon();
        InstanceMaterial im = new InstanceMaterial();
        materialSymbolId = getId("material_symbol");
        im.setSymbol(materialSymbolId);
        im.setTarget("#" + materialId);

        BindVertexInput bvi = new BindVertexInput();
        bvi.setSemantic("texcord");
        bvi.setInputSemantic("TEXCOORD");
        bvi.setInputSet(number(0));

        im.getBindVertexInputs().add(bvi);

        tc.getInstanceMaterials().add(im);
        bm.setTechniqueCommon(tc);
        ig.setBindMaterial(bm);

        node.getInstanceGeometries().add(ig);
        return materialSymbolId;
    }

    public void marsall(String fileName) throws Throwable {
        marshaller(this.c, fileName);
    }



    private Scene getOrCreateScene() {
        if (c.getScene() == null) {
            Scene le = new Scene();
            c.setScene(le);
        }
        return c.getScene();
    }

    private VisualScene getOrCreateVisualScene() {

        LibraryVisualScenes lvs = getOrCreateLibraryVisualScenes();

        if (lvs.getVisualScenes().size() > 0) {
            return lvs.getVisualScenes().get(0);
        }

        Scene scene = getOrCreateScene();

        VisualScene vs = new VisualScene();
        vs.setId(getId("VisualScene"));

        lvs.getVisualScenes().add(vs);

        InstanceWithExtra ivs = new InstanceWithExtra();
        ivs.setUrl("#" + vs.getId());

        scene.setInstanceVisualScene(ivs);

        return vs;
    }

    private LibraryGeometries getOrCreateLibraryGeometries() {
        for (Object object : this.c.getLibraryAnimationsAndLibraryAnimationClipsAndLibraryCameras()) {
            if (object instanceof LibraryGeometries) {
                return (LibraryGeometries) object;
            }
        }

        LibraryGeometries le = new LibraryGeometries();
        this.c.getLibraryAnimationsAndLibraryAnimationClipsAndLibraryCameras().add(le);
        return le;
    }

    private String getModelName(String source) {
        return getId("model_" + source == null ? "unnamed" : source);
    }

    /**
     * @return
     */
    public COLLADA createCollada() {
        COLLADA c = new COLLADA();
        c.setVersion("1.4.1");

        Asset asset = new Asset();
        asset.setTitle("kendzi 3d test");
        XMLGregorianCalendar date = getXMLGregorianCalendarNow();
        asset.setCreated(date);
        asset.setModified(date);

        Contributor contributor = new Contributor();
        contributor.setAuthoringTool("kendzi3d");
        asset.getContributors().add(contributor);
        asset.setUpAxis(UpAxisType.Y_UP);
        c.setAsset(asset);

        return c;
    }

    public XMLGregorianCalendar getXMLGregorianCalendarNow() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();

        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private LibraryVisualScenes getOrCreateLibraryVisualScenes() {
        for (Object object : this.c.getLibraryAnimationsAndLibraryAnimationClipsAndLibraryCameras()) {
            if (object instanceof LibraryVisualScenes) {
                return (LibraryVisualScenes) object;
            }
        }

        LibraryVisualScenes le = new LibraryVisualScenes();
        this.c.getLibraryAnimationsAndLibraryAnimationClipsAndLibraryCameras().add(le);
        return le;
    }


    private String convert(COLLADA c, Material mat) {

        String matId = this.matCatche.get(mat);
        if (matId != null) {
            return matId;
        }

        ProfileCOMMON pc = new ProfileCOMMON();
        String textureSampler = null;

        for (String textureKey : mat.getTexturesComponent()) {
            String textureFile = escapeTextureName(textureKey);
            this.textureKeys.put(textureKey, textureFile);
            Image imageId = createTextureImage(c, textureFile);
            String surfaceId = createTextureSurface(pc, imageId);
            String samplerId = createTextureSampler(pc, surfaceId);
            if (textureSampler == null) {
                // only first layer
                textureSampler = samplerId;
            }
        }

        Technique technique = new Technique();
        technique.setSid("common");

        Phong phong  = new Phong();

        if (textureSampler != null) {
            phong.setDiffuse(createTexture(textureSampler, "texcord"));
        } else {
            phong.setDiffuse(createColor(mat.getAmbientDiffuse().getDiffuseColor()));
        }
        phong.setAmbient(createColor(mat.getAmbientDiffuse().getAmbientColor()));

        technique.setPhong(phong);

        pc.setTechnique(technique);
        Effect effect = new Effect();
        effect.setId(getId("effect"));

        ObjectFactory of = new ObjectFactory();
        effect.getFxProfileAbstracts().add(of.createProfileCOMMON(pc));

        LibraryEffects le = getOrCreateLibEffects(c);
        le.getEffects().add(effect);

        LibraryMaterials lm = getOrCreateLibraryMaterials();
        org.collada._2005._11.colladaschema.Material material = new org.collada._2005._11.colladaschema.Material();
        material.setId(getId("material"));
        // material.setName(getId("material"));
        InstanceEffect ie = new InstanceEffect();
        ie.setUrl("#" + effect.getId());
        material.setInstanceEffect(ie);
        lm.getMaterials().add(material);

        matId = material.getId();
        this.matCatche.put(mat, matId);
        return matId;
    }

    private String escapeTextureName(String textureName) {
        if (textureName == null) {
            return null;
        }

        textureName = textureName.replaceAll("^/", "");
        textureName = textureName.replaceAll("^#bw=", "bw");

        return textureName;
    }



    private LibraryMaterials getOrCreateLibraryMaterials() {
        for (Object object : this.c.getLibraryAnimationsAndLibraryAnimationClipsAndLibraryCameras()) {
            if (object instanceof LibraryMaterials) {
                return (LibraryMaterials) object;
            }
        }
        LibraryMaterials le = new LibraryMaterials();
        this.c.getLibraryAnimationsAndLibraryAnimationClipsAndLibraryCameras().add(le);
        return le;
    }

    private Image createTextureImage(COLLADA c, String textureName) {
        LibraryImages li = getOrCreateLigraryImages(c);

        Image image = new Image();
        String imageId = getId("image");
        image.setId(imageId);
        image.setInitFrom(textureName);

        li.getImages().add(image);

        return image;

    }

    private static LibraryImages getOrCreateLigraryImages(COLLADA c) {

        for (Object object : c.getLibraryAnimationsAndLibraryAnimationClipsAndLibraryCameras()) {
            if (object instanceof LibraryImages) {
                return (LibraryImages) object;
            }
        }
        LibraryImages le = new LibraryImages();
        c.getLibraryAnimationsAndLibraryAnimationClipsAndLibraryCameras().add(le);
        return le;

    }

    private static String createTextureSampler(ProfileCOMMON pc,
            String surfaceId) {
        CommonNewparamType newparam = new CommonNewparamType();
        String samplerId = surfaceId + "-sampler";
        newparam.setSid(samplerId);


        FxSampler2DCommon sampler = new FxSampler2DCommon();
        sampler.setSource(surfaceId);
        newparam.setSampler2D(sampler);
        pc.getImagesAndNewparams().add(newparam);

        return samplerId;

    }

    private static String createTextureSurface(ProfileCOMMON pc,
            Image textureId) {

        CommonNewparamType newparam = new CommonNewparamType();
        String surfaceId = textureId.getId() + "-surface";
        newparam.setSid(surfaceId);

        FxSurfaceInitFromCommon init = new FxSurfaceInitFromCommon();
        init.setValue(textureId);

        FxSurfaceCommon surface = new FxSurfaceCommon();
        surface.setType("2D");
        surface.getInitFroms().add(init);
        newparam.setSurface(surface);

        pc.getImagesAndNewparams().add(newparam);

        return surfaceId;
    }


    private static String getTextureFileName(String textureName) {
        // TODO Auto-generated method stub
        return textureName;
    }

    private static CommonColorOrTextureType createColor(Color color) {
        CommonColorOrTextureType colorType = new CommonColorOrTextureType();
        org.collada._2005._11.colladaschema.CommonColorOrTextureType.Color c = new org.collada._2005._11.colladaschema.CommonColorOrTextureType.Color();
        float [] f = new float[4];
        color.getComponents(f);

        c.getValues().add((double) f[0]);
        c.getValues().add((double) f[1]);
        c.getValues().add((double) f[2]);
        c.getValues().add((double) f[3]);
        colorType.setColor(c);
        return colorType;
    }

    /**
     * @param textureSampler
     * @param texcord
     * @return
     */
    public static CommonColorOrTextureType createTexture(String textureSampler, String texcord) {
        CommonColorOrTextureType tex = new CommonColorOrTextureType();
        Texture texture = new Texture();
        texture.setTexture(textureSampler);
        texture.setTexcoord(texcord);
        tex.setTexture(texture);
        return tex;
    }

    private static LibraryEffects getOrCreateLibEffects(COLLADA c) {
        for (Object object : c.getLibraryAnimationsAndLibraryAnimationClipsAndLibraryCameras()) {
            if (object instanceof LibraryEffects) {
                return (LibraryEffects) object;
            }
        }
        LibraryEffects le = new LibraryEffects();
        c.getLibraryAnimationsAndLibraryAnimationClipsAndLibraryCameras().add(le);
        return le;
    }

    private static List<BigInteger>  convertToTriangles(int[] vertIndex, int type) {
        ArrayList<BigInteger> ret = new ArrayList<BigInteger>();

        if (type == FaceType.QUADS.getType()) {

            for (int offset =0; offset < vertIndex.length / 4; offset++ ) {
                int i = offset * 4;

                int i0 =  vertIndex[i];
                int i1 =  vertIndex[i + 1];
                int i2 =  vertIndex[i + 2];
                int i3 =  vertIndex[i + 3];

                ret.add(number(i0));
                ret.add(number(i1));
                ret.add(number(i2));

                ret.add(number(i0));
                ret.add(number(i2));
                ret.add(number(i3));
            }
            //            for (int i : vertIndex) {
            //                ret.add(number(i));
            //            }
        } else if (type == FaceType.TRIANGLES.getType()) {

            for (int offset = 0; offset < vertIndex.length; offset++ ) {

                int i0 =  vertIndex[offset];

                ret.add(number(i0));
            }
        } else if (type == FaceType.TRIANGLE_FAN.getType()) {

            for (int offset = 2; offset < vertIndex.length; offset++ ) {

                int i0 =  vertIndex[0];
                int i1 =  vertIndex[offset - 1];
                int i2 =  vertIndex[offset];

                ret.add(number(i0));
                ret.add(number(i1));
                ret.add(number(i2));
            }
        } else if (type == FaceType.QUAD_STRIP.getType()) {

            for (int offset = 1; offset < vertIndex.length / 2; offset++ ) {
                int i = offset * 2;

                int i0 =  vertIndex[i - 2];
                int i1 =  vertIndex[i - 1];
                int i2 =  vertIndex[i];
                int i3 =  vertIndex[i + 1];

                ret.add(number(i0));
                ret.add(number(i1));
                ret.add(number(i2));

                ret.add(number(i1));
                ret.add(number(i2));
                ret.add(number(i3));
            }

            //throw new RuntimeException("Face type : " + type + " not supported");
        } else {
            throw new RuntimeException("Face type : " + type + " not supported");
        }

        return ret;
    }

    @Override
    public void save(String fileName) throws Throwable {
        marsall(fileName);
        // String xml = marsall(fileName);
        //saveFile(fileName, xml);
    }


    static BigInteger number(int count) {
        return new BigInteger("" + count);
    }

    /**
     * @param mesh
     * @param face
     * @return
     */
    public Source createVertexSource(Point3d [] vertices/*, Face face*/) {
        FloatArray vertexArray = new FloatArray();
        String vertexArrayId = getId("v");
        vertexArray.setId(vertexArrayId);
        vertexArray.setCount(new BigInteger("" + vertices.length * 3));//face.vertIndex.length * 3));

        for (int vi = 0; vi < vertices.length; vi++) {
            Point3d point = vertices[vi];
            vertexArray.getValues().add(point.x);
            vertexArray.getValues().add(point.y);
            vertexArray.getValues().add(point.z);
        }

        Source vertexSource = new Source();
        vertexSource.setId(getId("vs"));
        vertexSource.setFloatArray(vertexArray);

        TechniqueCommon tc = new TechniqueCommon();
        Accessor acc = new Accessor();
        acc.setSource("#"+vertexArrayId);
        acc.setCount(new BigInteger("" + vertices.length));
        acc.setStride(new BigInteger("3"));

        Param parmX = new Param();
        parmX.setName("X");
        parmX.setType("float");
        acc.getParams().add(parmX);

        Param parmY = new Param();
        parmY.setName("Y");
        parmY.setType("float");
        acc.getParams().add(parmY);

        Param parmZ = new Param();
        parmZ.setName("Z");
        parmZ.setType("float");

        acc.getParams().add(parmZ);

        tc.setAccessor(acc);
        vertexSource.setTechniqueCommon(tc);
        return vertexSource;
    }

    /**
     * @param mesh
     * @param face
     * @return
     */
    public Source createNormalSource(Vector3d [] normals/*, Face face*/) {

        FloatArray normalsArray = new FloatArray();
        String normalsArrayId = getId("n");
        normalsArray.setId(normalsArrayId);
        normalsArray.setCount(new BigInteger("" + normals.length * 3));

        for (int vi = 0; vi < normals.length; vi++) {
            Vector3d v = normals[vi];
            normalsArray.getValues().add(v.x);
            normalsArray.getValues().add(v.y);
            normalsArray.getValues().add(v.z);
        }

        Source vertexSource = new Source();
        vertexSource.setId(getId("ns"));
        vertexSource.setFloatArray(normalsArray);

        TechniqueCommon tc = new TechniqueCommon();
        Accessor acc = new Accessor();
        acc.setSource("#"+normalsArrayId);
        acc.setCount(number(normals.length));
        acc.setStride(number(3));

        Param parmX = new Param();
        parmX.setName("X");
        parmX.setType("float");
        acc.getParams().add(parmX);

        Param parmY = new Param();
        parmY.setName("Y");
        parmY.setType("float");
        acc.getParams().add(parmY);

        Param parmZ = new Param();
        parmZ.setName("Z");
        parmZ.setType("float");

        acc.getParams().add(parmZ);

        tc.setAccessor(acc);
        vertexSource.setTechniqueCommon(tc);
        return vertexSource;
    }
    /**
     * @param mesh
     * @param face
     * @return
     */
    public Source createTexSource(TextCoord[] texCoords/*, Face face*/) {

        FloatArray texArray = new FloatArray();
        String normalsArrayId = getId("n");
        texArray.setId(normalsArrayId);
        texArray.setCount(number(texCoords.length * 2));

        for (int vi = 0; vi < texCoords.length; vi++) {
            TextCoord v = texCoords[vi];
            texArray.getValues().add(v.u);
            texArray.getValues().add(v.v);
        }

        Source vertexSource = new Source();
        vertexSource.setId(getId("ts"));
        vertexSource.setFloatArray(texArray);

        TechniqueCommon tc = new TechniqueCommon();
        Accessor acc = new Accessor();
        acc.setSource("#"+normalsArrayId);
        acc.setCount(number(texCoords.length));
        acc.setStride(number(2));

        Param parmX = new Param();
        parmX.setName("S");
        parmX.setType("float");
        acc.getParams().add(parmX);

        Param parmY = new Param();
        parmY.setName("T");
        parmY.setType("float");
        acc.getParams().add(parmY);

        tc.setAccessor(acc);
        vertexSource.setTechniqueCommon(tc);
        return vertexSource;
    }


    protected String getId(String prefix) {
        this.id++;
        return "ID_" + prefix + "_"+ this.id;
    }



    public static void marshaller(COLLADA c, String fileName) throws JAXBException {
        // =============================================================================================================
        // Setup JAXB
        // =============================================================================================================

        // Create a JAXB context passing in the class of the object we want to marshal/unmarshal
        final JAXBContext context = JAXBContext.newInstance(COLLADA.class);

        // =============================================================================================================
        // Marshalling OBJECT to XML
        // =============================================================================================================

        // Create the marshaller, this is the nifty little thing that will actually transform the object into XML
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        // Create a stringWriter to hold the XML
        final StringWriter stringWriter = new StringWriter();



        // Marshal the javaObject and write the XML to the stringWriter
        marshaller.marshal(c, new File(fileName));//stringWriter);

        // Print out the contents of the stringWriter
        return ;
        //        // =============================================================================================================
        //        // Unmarshalling XML to OBJECT
        //        // =============================================================================================================
        //
        //        // Create the unmarshaller, this is the nifty little thing that will actually transform the XML back into an object
        //        final Unmarshaller unmarshaller = context.createUnmarshaller();
        //
        //        // Unmarshal the XML in the stringWriter back into an object
        //        final JavaObject javaObject2 = (JavaObject) unmarshaller.unmarshal(new StringReader(stringWriter.toString()));
        //
        //        // Print out the contents of the JavaObject we just unmarshalled from the XML
        //        System.out.println(javaObject2.toString());
    }

}
