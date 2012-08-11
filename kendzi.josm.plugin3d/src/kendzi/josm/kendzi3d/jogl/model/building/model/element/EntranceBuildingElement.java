package kendzi.josm.kendzi3d.jogl.model.building.model.element;


public class EntranceBuildingElement extends SquareHoleElement {

    String entranceType;

    /**
     *
     */
    public EntranceBuildingElement() {

        super(0, 2.36, 1.2);




//        super();
//        this.minHeight = 1;
//
//        double height = 1.5;
//        this.height = this.minHeight + height;
//
//        this.width = 1.8;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {

        EntranceBuildingElement wbe = (EntranceBuildingElement) super.clone();

        wbe.setEntranceType(getEntranceType());
        return wbe;
    }


    /**
     * @return the entranceType
     */
    public String getEntranceType() {
        return entranceType;
    }

    /**
     * @param entranceType the entranceType to set
     */
    public void setEntranceType(String entranceType) {
        this.entranceType = entranceType;
    }

}
