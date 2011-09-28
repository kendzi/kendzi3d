/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.validation;

import java.util.ArrayList;
import java.util.List;

import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;

/**
 * Validator for model.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public final class ValidationUtil {

    /**
     * It is util.
     */
    private ValidationUtil() {
        //
    }

    public static String errorToString(List<String> errors) {
        StringBuffer sb = new StringBuffer();
        sb.append("model errors:");
        if (errors == null) {
            return sb.toString();
        }
        for (String error : errors) {
            sb.append("\n");
            sb.append(error);
        }
        return sb.toString();
    }

    /** Validate model.
     * @param pModel model
     * @return list of errors
     */
    public static List<String> validate(Model pModel) {
        return validate(pModel, false);
    }

    /** Validate model. If it is possible tray to fix it. Goal of fixing is to render model without exception.
     * @param pModel model
     * @param pRepair try to fix
     * @return list of errors
     */
    public static List<String> validate(Model pModel, boolean pRepair) {
        List<String> ret = new ArrayList<String>();

        int mi = 0;
        int fi = 0;
        for (mi = 0; mi < pModel.mesh.length; mi++) {
            Mesh mesh = pModel.mesh[mi];

            for (fi = 0; fi < mesh.face.length; fi++) {
                Face face = mesh.face[fi];

                if (face.vertIndex == null) {
                    ret.add(faceDescription("has null vertex index array", pModel, mi, fi));
                    if (pRepair) {
                        face.vertIndex = new int[0];
                    } else {
                        continue;
                    }
                }

                if (face.normalIndex != null) {
                    if (face.normalIndex.length < face.vertIndex.length) {

                        ret.add(faceDescription("less normals index then vertex indexs", pModel, mi, fi));
                        if (pRepair) {
                            ret.add(faceDescription("turning off normals", pModel, mi, fi));
                            face.normalIndex = null;
                        } else {
                            continue;
                        }
                    }

                }

                if (mesh.hasTexture) {

                    if (mesh.texCoords == null) {
                        ret.add(faceDescription(
                                "mesh hasTexture is on, but ther is no texture coords", pModel, mi, fi));
                        if (pRepair) {
                            ret.add(faceDescription("turning off hasTexture", pModel, mi, fi));
                            mesh.hasTexture = false;
                        } else {
                            continue;
                        }
                    } else if (face.coordIndex.length > face.vertIndex.length) {

                        ret.add(faceDescription("less texCoords indexs then vertex indexs", pModel, mi, fi));
                        if (pRepair) {
                            ret.add(faceDescription("urning off hasTexture", pModel, mi, fi));
                            mesh.hasTexture = false;
                        } else {
                            continue;
                        }
                    }
                }


                for (int vi = 0; vi < face.vertIndex.length; vi++) {
                    int vetexIndex = face.vertIndex[vi];
                    if (mesh.vertices.length < vetexIndex) {
                        ret.add(faceVertexDescription("vertex index biger then size of vertex array in model: "
                                + mesh.vertices.length, pModel, mi, fi, vi));
                        if (pRepair) {
                            ret.add(faceVertexDescription("turning off face vertexs indexes", pModel, mi, fi, vi));
                            face.vertIndex = new int [0];
                            break;
                        } else {
                            continue;
                        }
                    }

                    if (face.normalIndex != null) {
                        int normalIndex = face.normalIndex[vi];
                        if (mesh.normals.length < normalIndex) {
                            ret.add(faceVertexDescription("normal index biger then size of normals array in model: "
                                    + mesh.normals.length, pModel, mi, fi, vi));
                            if (pRepair) {
                                ret.add(faceVertexDescription("trim normal index to normals array size", pModel, mi,
                                        fi, vi));
                                face.normalIndex[vi] = normalIndex % mesh.normals.length;
                                break;
                            } else {
                                continue;
                            }
                        }
                    }

                    if (mesh.hasTexture) {
                        int textureIndex = face.coordIndex[vi];
                        if (mesh.texCoords.length < textureIndex) {
                            ret.add(faceVertexDescription(
                                    "textureIndex index biger then size of texCoords array in model: "
                                            + mesh.texCoords.length, pModel, mi, fi, vi));
                            if (pRepair) {
                                ret.add(faceVertexDescription("urning off hasTexture", pModel, mi, fi, vi));
                                mesh.hasTexture = false;
                            } else {
                                continue;
                            }
                        }
                    }
                }
            }
        }
        return ret;

    }

    /**
     * @param pErrorMsg error message
     * @param pModel model
     * @param pMi mesh index
     * @param pFi face index
     * @return error message
     */
    private static String faceDescription(String pErrorMsg, Model pModel, int pMi, int pFi) {
        return "model: " + pModel.getSource() + " mesh: " + pMi + ", " + pModel.mesh[pMi].name + " face: " + pFi
                + ", " + pErrorMsg;
    }
    /**
     * @param pErrorMsg error message
     * @param pModel model
     * @param pMi mesh index
     * @param pFi face index
     * @param pVi vertex index
     * @return error message
     */
    private static String faceVertexDescription(String pErrorMsg, Model pModel, int pMi, int pFi, int pVi) {
        return "model: " + pModel.getSource() + " mesh: " + pMi + ", " + pModel.mesh[pMi].name + " face: " + pFi
            + ", vertex index: " + pVi + ", " + pErrorMsg;
    }
}
