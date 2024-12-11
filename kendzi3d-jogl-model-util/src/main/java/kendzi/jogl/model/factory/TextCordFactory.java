/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.factory;

import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.math.geometry.Algebra;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class TextCordFactory {

    /**
     * Texture projection on surface.
     *
     * @param pPointToCalc
     *            to calculates texture coordinates
     * @param pPlaneNormal
     *            normal vector of surface plane
     * @param pLineVector
     *            vector laying on the plane (texture is parallel to this vector)
     * @param pStartPoint
     *            point when texture starts, laying on surface
     * @param pTexture
     *            texture
     * @return uv cordinates for texture
     */
    public static TextCoord calcFlatSurfaceUV(Vector3dc pPointToCalc, Vector3dc pPlaneNormal, Vector3dc pLineVector,
            Vector3dc pStartPoint, TextureData pTexture) {
        return calcFlatSurfaceUV(pPointToCalc, pPlaneNormal, pLineVector, pStartPoint, pTexture, 0, 0);
    }

    /**
     * Texture projection on surface.
     *
     * @param pPointToCalc
     *            to calculates texture coordinates
     * @param pPlaneNormal
     *            normal vector of surface plane
     * @param pLineVector
     *            vector laying on the plane (texture is parallel to this vector)
     * @param pStartPoint
     *            point when texture starts, laying on surface
     * @param pTexture
     *            texture
     * @param textureOffsetU
     *            offset for texture U
     * @param textureOffsetV
     *            offset for texture V
     * @return uv cordinates for texture
     */
    public static TextCoord calcFlatSurfaceUV(Vector3dc pPointToCalc, Vector3dc pPlaneNormal, Vector3dc pLineVector,
            Vector3dc pStartPoint, TextureData pTexture, double textureOffsetU, double textureOffsetV) {

        Vector3dc base = new Vector3d(pStartPoint).negate();
        Vector3dc p = new Vector3d(pPointToCalc).add(base);

        Vector3dc orthogonalProjectionU = Algebra.orthogonalProjection(pLineVector, p);

        double u = orthogonalProjectionU.length() / pTexture.getWidth();

        if (pLineVector.dot(orthogonalProjectionU) < 0) {
            u = u * -1;
        }

        Vector3dc cross = pPlaneNormal.cross(pLineVector, new Vector3d());

        // cross.cross(pLineVector, pPlaneNormal);

        Vector3dc orthogonalProjectionV = Algebra.orthogonalProjection(cross, p);

        double v = orthogonalProjectionV.length() / pTexture.getHeight();

        if (cross.dot(orthogonalProjectionV) < 0) {
            v = v * -1;
        }

        u = u + textureOffsetU / pTexture.getWidth();
        v = v + textureOffsetV / pTexture.getHeight();

        return new TextCoord(u, v);
    }
}
