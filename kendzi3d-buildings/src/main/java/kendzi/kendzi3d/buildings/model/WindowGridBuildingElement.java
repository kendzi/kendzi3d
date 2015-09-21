package kendzi.kendzi3d.buildings.model;

import kendzi.kendzi3d.buildings.model.element.WindowBuildingElement;

public class WindowGridBuildingElement extends WindowBuildingElement implements BuildingWallElement{

    private int numOfCols;

    //int numOfRows;

    /**
     * @param numOfCols
     */
    public WindowGridBuildingElement(int numOfCols) {
        super();
        this.numOfCols = numOfCols;
    }

    /**
     * @return the numOfCols
     */
    public int getNumOfCols() {
        return numOfCols;
    }

    /**
     * @param numOfCols the numOfCols to set
     */
    public void setNumOfCols(int numOfCols) {
        this.numOfCols = numOfCols;
    }


}
