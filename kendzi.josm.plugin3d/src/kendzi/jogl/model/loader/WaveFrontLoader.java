package kendzi.jogl.model.loader;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.BoundsFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.Bounds;
import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.Material;
import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.josm.kendzi3d.service.FileUrlReciverService;
import kendzi.math.geometry.Normal;
import net.java.joglutils.model.ModelLoadException;

import org.apache.log4j.Logger;

/**
 *
 * @author RodgersGB
 * @author Tomasz Kedziora (Kendzi)
 */
public class WaveFrontLoader implements iLoader {

    /** Log. */
    private static final Logger log = Logger.getLogger(WaveFrontLoader.class);

    public static final String VERTEX_DATA = "v ";
    public static final String NORMAL_DATA = "vn ";
    public static final String TEXTURE_DATA = "vt ";
    public static final String FACE_DATA = "f ";
    public static final String SMOOTHING_GROUP = "s ";
    public static final String GROUP = "g ";
    public static final String OBJECT = "o ";
    public static final String COMMENT = "#";
    public static final String EMPTY = "";

    int vertexTotal = 0;
    int textureTotal = 0;
    int normalTotal = 0;

    private DataInputStream dataInputStream;
    // the model
    private Model model = null;
    /** Bounds of the model. */
//    private Bounds bounds = new Bounds();
    /** Center of the model. */
//    private Point3d center = new Point3d(0.0f, 0.0f, 0.0f);
    private String baseDir = null;

    /** Creates a new instance of myWaveFrontLoader. */
    public WaveFrontLoader() {
    }

    int numComments = 0;
    private boolean rebildNormals = false;
    private String lastLineToProcess;
    private BoundsFactory boundsFactory;

    @Override
    public Model load(String path) throws ModelLoadException {
        this.boundsFactory = new BoundsFactory();
        this.model = new Model(path);
        Mesh mesh = null;

        this.baseDir = "";
        path = path.replaceAll("\\\\", "/");
        String [] tokens = path.split("/");
        for (int i = 0; i < tokens.length - 1; i++) {
            this.baseDir += tokens[i] + "/";
        }

        InputStream stream = null;
        try {

//            stream = ResourceRetriever.getResourceAsInputStream(model.getSource());
           URL modelURL = FileUrlReciverService.reciveFileUrl(this.model.getSource());

           if (modelURL != null) {
               stream = modelURL.openStream();
           }
           if (stream == null) {
               throw new ModelLoadException("Stream is null");
           }
        } catch (IOException e) {
            throw new ModelLoadException("Caught IO exception: " + e);
        }

        try {
            // Open a file handle and read the models data
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String  line = null;
            while (((line = this.lastLineToProcess) != null) || ((line = br.readLine()) != null)) {
                this.lastLineToProcess = null;

                if (lineIs(COMMENT, line)) {
                    // ignore comments
                    this.numComments++;
                    continue;
                }

                if (line.length() == 0) {
                    // igonore empty lines
                    continue;
                }

                if (lineIs(GROUP, line)) {
                    if (mesh == null) {
                        mesh = new Mesh();
                    }

                    mesh.name = parseName(line);
                }

                if (lineIs(OBJECT, line)) {

                }

                if (lineIs(VERTEX_DATA, line)) {
                    if (mesh == null) {
                        mesh = new Mesh();
                    }

                    mesh.vertices = (Point3d[]) getPoints(VERTEX_DATA, line, br);
                    mesh.numOfVerts = mesh.vertices.length;
                }

                if (lineIs(TEXTURE_DATA, line)) {
                    if (mesh == null) {
                        mesh = new Mesh();
                    }

                    mesh.texCoords = getTexCoords(TEXTURE_DATA, line, br);
                    mesh.hasTexture = true;
                    mesh.numTexCoords = mesh.texCoords.length;
                }

                if (lineIs(NORMAL_DATA, line)) {
                    if (mesh == null) {
                        mesh = new Mesh();
                    }

                    Vector3d [] normals = (Vector3d []) getPoints(NORMAL_DATA, line, br);
                    if (!this.rebildNormals) {
                        mesh.normals = normals;
                    }
                }

                if (lineIs(FACE_DATA, line)) {
                    if (mesh == null) {
                        mesh = new Mesh();
                    }

                    mesh.face = getFaces(line, mesh, br);
                    mesh.numOfFaces = mesh.face.length;

                    //FIXME
                    if (this.model.mesh == null) {
                        this.model.mesh = new Mesh [0];
                    }

                    Mesh [] ma = new Mesh[ this.model.mesh.length + 1];

                    System.arraycopy(this.model.mesh, 0, ma, 0, this.model.mesh.length);
                    ma[ma.length - 1] = mesh;
                    // FIXME
                    this.model.mesh = ma;

                    //                    model.addMesh(mesh);
                    mesh = new Mesh();
                }

                if (lineIs("mtllib ", line)) {
                    processMaterialLib(line);
                }

                if (lineIs("usemtl ", line)) {
                    processMaterialType(line, mesh);
                }
            }
        } catch (IOException e) {
            throw new ModelLoadException("Failed to find or read OBJ: " + stream);
        }


        // FIXME
        if (mesh.vertices != null) {
            Mesh [] ma = new Mesh[ this.model.mesh.length + 1];

            System.arraycopy(this.model.mesh, 0, ma, 0, this.model.mesh.length);
            ma[ma.length - 1] = mesh;
            // FIXME
            this.model.mesh = ma;
        }
        //        model.addMesh(mesh);


        mesh = null;

        for (int i = 0; i < this.model.getNumberOfMaterials(); i++) {
            Material material = this.model.getMaterial(i);
            if (material.strFile != null) {
                material.strFile = getFullTexturePath(this.model.getSource()) + material.strFile;
            }
        }

        Bounds bounds = this.boundsFactory.toBounds();
        log.info(bounds);
        this.model.setBounds(bounds);
        this.model.setCenterPoint(bounds.center);

        return this.model;
    }

    /** Get full texture path. Textures in obj files are described by file name.
     * This function add to texture name prefix taken from model location.
     * @param pModelFile path to model
     * @return path in project
     */
    public String getFullTexturePath(String pModelFile) {
        String subFileName = "";

        // If this is read from a jar file, then try to find the path relative to the model
        int index = pModelFile.lastIndexOf('/');
        if (index != -1) {
            subFileName = pModelFile.substring(0, index + 1);
        } else {
            // Else, the file path of the model was not from a jar file, so check maybe it
            // was from a local file and get that path.
            index = pModelFile.lastIndexOf('\\');

            if (index != -1) {
                subFileName = pModelFile.substring(0, index + 1);
            }
        }
        return subFileName;
    }


    private boolean lineIs(String type, String line) {
        return line.startsWith(type);
    }

    private Tuple3d[] getPoints(String prefix, String currLine, BufferedReader br) throws IOException {
        Vector<Tuple3d> points = new Vector<Tuple3d>();
        boolean isVertices = prefix.equals(VERTEX_DATA);

        // we've already read in the first line (currLine)
        // so go ahead and parse it
        if (isVertices) {
            points.add(parsePoint(currLine));
        } else {
            points.add(parseVector(currLine));
        }

        // parse through the rest of the points
        String line = null;
        while ((line = br.readLine()) != null) {
            if (!lineIs(prefix, line)) {
                this.lastLineToProcess = line;
                break;
            }

            if (isVertices) {
                Point3d point = parsePoint(line);
                // Calculate the bounds for the entire model
                this.boundsFactory.addPoint(point);
                points.add(point);
            } else {
                Vector3d point = parseVector(line);
                points.add(point);
            }
        }

//        if (isVertices) {
//            // Calculate the center of the model
//            this.center.x = 0.5f * (this.bounds.max.x + this.bounds.min.x);
//            this.center.y = 0.5f * (this.bounds.max.y + this.bounds.min.y);
//            this.center.z = 0.5f * (this.bounds.max.z + this.bounds.min.z);
//        }

        // return the points
        if (isVertices) {
            Point3d [] values = new Point3d[points.size()];
            return points.toArray(values);
        } else {
            Vector3d [] values = new Vector3d[points.size()];
            return points.toArray(values);

        }
    }

    private void setLineToProcess(String line) {
        this.lastLineToProcess = line;
    }

    private TextCoord[] getTexCoords(String prefix, String currLine, BufferedReader br) throws IOException {
        Vector<TextCoord> texCoords = new Vector<TextCoord>();

        String [] s = currLine.split("\\s+");
        TextCoord texCoord = new TextCoord();
        texCoord.u = Float.parseFloat(s[1]);
        texCoord.v = Float.parseFloat(s[2]);

        texCoords.add(texCoord);

        // parse through the rest of the points
        String line = null;
        while ((line = br.readLine()) != null) {
            if (!lineIs(prefix, line)) {
                this.lastLineToProcess = line;
                break;
            }

            s = line.split("\\s+");

            texCoord = new TextCoord();
            texCoord.u = Float.parseFloat(s[1]);
            texCoord.v = Float.parseFloat(s[2]);

            texCoords.add(texCoord);
        }

        // return the texture coordinates
        TextCoord [] values = new TextCoord[texCoords.size()];
        return texCoords.toArray(values);
    }

//    String markAndRead(BufferedReader br) {
//        br.mark(readAheadLimit)
//    }

    private Face[] getFaces(String currLine, Mesh mesh, BufferedReader br) throws IOException {
        Vector<Face> faces = new Vector<Face>();

        Set<Face> smoothingGroup = new HashSet<Face>();
        boolean smoothing = false;

        faces.add(parseFace(currLine));

        // parse through the rest of the faces
        String line = null;
        while ((line = br.readLine()) != null) {
            if (lineIs(SMOOTHING_GROUP, line)) {
                if (startSmoothingGroup(line)) {
                    smoothing = true;
                }
                continue;
            } else if (lineIs("usemtl ", line)) {
                processMaterialType(line, mesh);
            } else if (lineIs(FACE_DATA, line)) {
                Face face = parseFace(line);
                if (smoothing) {
                    smoothingGroup.add(face);
                }
                faces.add(face);
            } else {
                this.lastLineToProcess = line;
                break;
            }
        }

        // move at end of model generation
        if (this.rebildNormals) {
            List<Vector3d> normals = new ArrayList<Vector3d>();

            for (Face face : faces) {
                if (face.vertIndex.length > 2) {
                    Vector3d normal = Normal.calcNormalNorm(
                            mesh.vertices[face.vertIndex[0]],
                            mesh.vertices[face.vertIndex[1]],
                            mesh.vertices[face.vertIndex[2]]);
                    normals.add(normal);
                    int ni = normals.indexOf(normal);

                    face.normalIndex = new int[face.vertIndex.length];
                    for (int vi = 0; vi < face.vertIndex.length; vi++) {
                        face.normalIndex[vi] = ni;
                    }
                } else {
                    log.error("Ups face don't have three vertex, can't calc new normal vector");
//                    ??
                }
            }
            if (mesh.normals == null) {
                mesh.normals = new Vector3d[0];
            }
            Vector3d [] newNormals = new Vector3d[mesh.normals.length + normals.size()];
            System.arraycopy(mesh.normals, 0, newNormals, 0, mesh.normals.length);

            int s = mesh.normals.length;
            for (int i = 0; i < normals.size(); i++) {
                newNormals[s + i] = normals.get(i);
            }
            mesh.normals = newNormals;
        }

        // return the faces
        Face [] values = new Face[faces.size()];
        return faces.toArray(values);
    }

    private boolean startSmoothingGroup(String line) {
        String [] s = line.split("\\s+");
        if (s.length > 1) {
            if ("1".equals(s[1])) {
                return true;
            }
        }
        return false;
    }

    private Face parseFace(String line) {
        String [] s = line.split("\\s+");
        if (line.contains("//")) { // Pattern is present if obj has no texture
            for (int loop = 1; loop < s.length; loop++) {
                s[loop] = s[loop].replaceAll("//","/-1/"); //insert -1 for missing vt data
            }
        }

        int[] vdata = new int[s.length - 1];
        int[] vtdata = new int[s.length - 1];
        int[] vndata = new int[s.length - 1];
        int type = FaceType.TRIANGLES.getType(); //XXX
        if (s.length > 4) { //XXX
            type = FaceType.TRIANGLE_FAN.getType();
        }
        Face face = new Face(type, s.length - 1);

        for (int loop = 1; loop < s.length; loop++) {
            String s1 = s[loop];
            String[] temp = s1.split("/");

            if (temp.length > 0) { // we have vertex data
                if (Integer.valueOf(temp[0]) < 0) {
                    // TODO handle relative vertex data
                } else {
                    face.vertIndex[loop - 1] = Integer.valueOf(temp[0]) - 1 - this.vertexTotal;
                    // log.info("found vertex index: " + face.vertIndex[loop-1]);
                }
            }

            if (temp.length > 1) { // we have texture data
                if (Integer.valueOf(temp[1]) < 0) {
                    face.coordIndex[loop - 1] = 0;
                } else {
                    face.coordIndex[loop - 1] = Integer.valueOf(temp[1]) - 1 - this.textureTotal;
                    // log.info("found texture index: " + face.coordIndex[loop-1]);
                }
            }

            if (temp.length > 2) { // we have normal data
                face.normalIndex[loop - 1] = Integer.valueOf(temp[2]) - 1 - this.normalTotal;
                // log.info("found normal index: " + face.normalIndex[loop-1]);
            }
        }

        return face;
    }

    private Point3d parsePoint(String line) {
        Point3d point = new Point3d();

        final String [] s = line.split("\\s+");

        point.x = Float.parseFloat(s[1]);
        point.y = Float.parseFloat(s[2]);
        point.z = Float.parseFloat(s[3]);

        return point;
    }

    private Vector3d parseVector(String line) {
        Vector3d point = new Vector3d();

        final String [] s = line.split("\\s+");

        point.x = Float.parseFloat(s[1]);
        point.y = Float.parseFloat(s[2]);
        point.z = Float.parseFloat(s[3]);

        return point;
    }

    private String parseName(String line) {
        String name;

        final String [] s = line.split("\\s+");

        name = s[1];

        return name;
    }

    private void processMaterialLib(String mtlData) {
        String [] s = mtlData.split("\\s+");

        Material mat = new Material();
        InputStream stream = null;
        try {
            URL materialURL = FileUrlReciverService.reciveFileUrl(this.baseDir + s[1]);
            stream = materialURL.openStream();

//            stream = ResourceRetriever.getResourceAsInputStream(this.baseDir + s[1]);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if(stream == null) {
            try {
                stream = new FileInputStream(this.baseDir + s[1]);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                return;
            }
        }
        loadMaterialFile(stream);
    }

    private void processMaterialType(String line, Mesh mesh) {
        String [] s = line.split("\\s+");

        int materialID = -1;
        boolean hasTexture = false;

        for(int i = 0; i < this.model.getNumberOfMaterials(); i++){
            Material mat = this.model.getMaterial(i);

            if(mat.strName.equals(s[1])){
                materialID = i;
                if(mat.strFile != null) {
                    hasTexture = true;
                } else {
                    hasTexture = false;
                }
                break;
            }
        }

        if (materialID != -1) {
            mesh.materialID = materialID;
        }
    }

    public Material loadMaterialFile(InputStream stream) {
        Material mat = null;
        int texId = 0;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;

            while ((line = br.readLine()) != null) {

                String [] parts = line.trim().split("\\s+");

                if (parts[0].equals("newmtl")) {
                    if (mat != null) {
                        this.model.addMaterial(mat);
                    }

                    mat = new Material();
                    mat.strName = parts[1];
                    mat.textureId = texId++;

                } else if (parts[0].equals("Ks")) {
                    mat.specularColor = parseColor(line);
                } else if (parts[0].equals("Ns")) {
                    if (parts.length > 1) {
                        mat.shininess = Float.valueOf(parts[1]);
                    }
                } else if (parts[0].equals("d")) {
                    ;
                } else if (parts[0].equals("illum")) {
                    ;
                } else if (parts[0].equals("Ka")) {
                    mat.ambientColor = parseColor(line);
                } else if (parts[0].equals("Kd")) {
                    mat.diffuseColor = parseColor(line);
                } else if (parts[0].equals("map_Kd")) {
                    if (parts.length > 1) {
                        mat.strFile = /* baseDir + */parts[1];
                    }
                } else if (parts[0].equals("map_Ka")) {
                    if (parts.length > 1) {
                        mat.strFile = /* baseDir + */parts[1];
                    }
                }
            }

            br.close();
            this.model.addMaterial(mat);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return mat;
    }

    private Color parseColor(String line) {
        String parts[] = line.trim().split("\\s+");

        Color color = new Color(Float.valueOf(parts[1]), Float.valueOf(parts[2]), Float.valueOf(parts[3]));

        return color;
    }

    public static void main(String[] args) {
        WaveFrontLoader loader = new WaveFrontLoader();
        try {
            loader.load("C:\\dane_tomekk\\eclipse\\workspace2\\kendzi.josm.kendzi3d\\models\\obj\\tree1.obj");
            //loader.load("C:\\Documents and Settings\\RodgersGB\\My Documents\\Projects\\JOGLUTILS\\src\\net\\java\\joglutils\\examples\\models\\obj\\penguin.obj");
        } catch (ModelLoadException ex) {
            ex.printStackTrace();
        }
    }
}
