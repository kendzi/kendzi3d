/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.DormerTypeBuilder;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofDormerType;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofDormerTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofHookPoint;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRow;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerType;


public class RectangleRoofHooksSpaces implements RoofHooksSpaces {

    PolygonRoofHooksSpace frontSpace;
    PolygonRoofHooksSpace rightSpace;
    PolygonRoofHooksSpace backSpace;
    PolygonRoofHooksSpace leftSpace;

    @Override
    public List<RoofHooksSpace> getRoofHooksSpaces() {

        return Arrays.asList((RoofHooksSpace) frontSpace, (RoofHooksSpace) rightSpace, (RoofHooksSpace) backSpace,
                (RoofHooksSpace) leftSpace);
    }



    @Override
    public List<RoofDormerTypeOutput> buildDormers(DormerRoofModel pRoof, RoofTextureData pRoofTextureData) {

        List<RoofHooksSpace> pRoofHooksSpace = getRoofHooksSpaces();


        List<RoofDormerTypeOutput> ret = new ArrayList<RoofDormerTypeOutput>();


        if (areDormers(pRoof.getDormersBack())
            || areDormers(pRoof.getDormersFront())
            || areDormers(pRoof.getDormersLeft())
            || areDormers(pRoof.getDormersRight())) {
            // method 1

            build(pRoof.getDormersFront() , this.frontSpace, ret, pRoof, pRoofTextureData);
            build(pRoof.getDormersBack() , this.backSpace, ret, pRoof, pRoofTextureData);
            build(pRoof.getDormersRight() , this.rightSpace, ret, pRoof, pRoofTextureData);
            build(pRoof.getDormersLeft() , this.leftSpace, ret, pRoof, pRoofTextureData);

        } else {
            // method 2 Deprecated?

            List<List<DormerType>> dormers = pRoof.getDormers();

            for (int i = 0; i < pRoofHooksSpace.size(); i++) {
                RoofHooksSpace space = pRoofHooksSpace.get(i);

//                char[] extensionTypes = getExtensionType(i, roofExtensions);
                List<DormerType>  dormersOnSpace = getExtensionType(i, dormers);

                build(dormersOnSpace, DormerRow.ROW_1, 1, space, ret, pRoof, pRoofTextureData);
            }
        }

        return ret;
    }

    private boolean areDormers(Map<DormerRow, List<DormerType>> dormers) {
        if (dormers == null || dormers.isEmpty()) {
            return false;
        }
        return true;
    }





    private void build(Map<DormerRow, List<DormerType>> dormersOnSpace, RoofHooksSpace space,
            List<RoofDormerTypeOutput> ret, DormerRoofModel pRoof, RoofTextureData pRoofTextureData) {


        int dormerRowNum = findMaxRow(dormersOnSpace.keySet());

        for (DormerRow dormerRow : dormersOnSpace.keySet()) {
            List<DormerType> dormersOnRow = dormersOnSpace.get(dormerRow);

            build(dormersOnRow, dormerRow, dormerRowNum, space, ret, pRoof, pRoofTextureData);

        }

    }


    private int findMaxRow(Set<DormerRow> keySet) {

        int maxRow = 1;

        for (DormerRow dormerRow : keySet) {
            int row = dormerRow.getRowNum();
            if (row > maxRow) {
                maxRow = row;
            }
        }
        return maxRow;
    }

    /**
     * @param dormersOnSpace
     * @param space
     * @param ret
     * @param pRoof
     * @param pRoofTextureData
     */
    private void build(List<DormerType> dormersOnSpace, DormerRow dormerRow, int dormerRowNum, RoofHooksSpace space, List<RoofDormerTypeOutput> ret,
            DormerRoofModel pRoof, RoofTextureData pRoofTextureData) {

        if (space == null) {
            return;
        }



        RoofHookPoint[] roofHookPoints = space.getRoofHookPoints(dormersOnSpace.size(), dormerRow, dormerRowNum);

        for (int ei = 0; ei < dormersOnSpace.size() && ei < roofHookPoints.length; ei++) {

            RoofDormerType roofType = getRoofExtansionType(dormersOnSpace.get(ei));

            RoofDormerTypeOutput buildRoof = roofType.buildRoof(roofHookPoints[ei], space, pRoof.getMeasurements(), pRoofTextureData);

            ret.add(buildRoof);

        }

    }

    private List<DormerType> getExtensionType(int i, List<List<DormerType>> roofExtensions) {
        if (roofExtensions == null) {
            return new ArrayList<DormerType>();
        }

        if (i >= roofExtensions.size()) {
            return new ArrayList<DormerType>();
        }

        List<DormerType> characters = roofExtensions.get(i);
        if (characters == null) {
            characters = new ArrayList<DormerType>();
        }
        return characters;
    }

    private static RoofDormerType getRoofExtansionType(DormerType pKey) {
        if (pKey == null) {
            return null;
        }
        for (RoofDormerType rt : DormerTypeBuilder.dormerTypeBuilders) {
            if (pKey.equals(rt.getType())) {
                return rt;
            }
        }
        return null;
    }


    /**
     * @param frontSpace the frontSpace to set
     */
    public void setFrontSpace(PolygonRoofHooksSpace frontSpace) {
        this.frontSpace = frontSpace;
    }



    /**
     * @param rightSpace the rightSpace to set
     */
    public void setRightSpace(PolygonRoofHooksSpace rightSpace) {
        this.rightSpace = rightSpace;
    }



    /**
     * @param backSpace the backSpace to set
     */
    public void setBackSpace(PolygonRoofHooksSpace backSpace) {
        this.backSpace = backSpace;
    }



    /**
     * @param leftSpace the leftSpace to set
     */
    public void setLeftSpace(PolygonRoofHooksSpace leftSpace) {
        this.leftSpace = leftSpace;
    }



}
