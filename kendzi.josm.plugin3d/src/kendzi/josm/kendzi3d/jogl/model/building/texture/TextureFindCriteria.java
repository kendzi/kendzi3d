package kendzi.josm.kendzi3d.jogl.model.building.texture;

import kendzi.josm.kendzi3d.jogl.model.building.texture.BuildingElementsTextureMenager.Type;

public class TextureFindCriteria {
    private Type type;
    private String typeName;
    private String subTypeName;
    private Double width;
    private Double height;
    private boolean colorable;

    public TextureFindCriteria(Type pType, String typeName, String subTypeName, Double width, Double height, boolean colorable) {
        super();
        this.type = pType;
        this.typeName = typeName;
        this.subTypeName = subTypeName;
        this.width = width;
        this.height = height;
        this.colorable = colorable;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return this.type;
    }



    /**
     * @param type the pType to set
     */
    public void setType(Type type) {
        this.type = type;
    }



    /**
     * @return the typeName
     */
    public String getTypeName() {
        return this.typeName;
    }



    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }



    /**
     * @return the subTypeName
     */
    public String getSubTypeName() {
        return this.subTypeName;
    }



    /**
     * @param subTypeName the subTypeName to set
     */
    public void setSubTypeName(String subTypeName) {
        this.subTypeName = subTypeName;
    }



    /**
     * @return the width
     */
    public Double getWidth() {
        return this.width;
    }



    /**
     * @param width the width to set
     */
    public void setWidth(Double width) {
        this.width = width;
    }



    /**
     * @return the height
     */
    public Double getHeight() {
        return this.height;
    }



    /**
     * @param height the height to set
     */
    public void setHeight(Double height) {
        this.height = height;
    }



    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.height == null) ? 0 : this.height.hashCode());
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + ((this.subTypeName == null) ? 0 : this.subTypeName.hashCode());
        result = prime * result + ((this.typeName == null) ? 0 : this.typeName.hashCode());
        result = prime * result + ((this.width == null) ? 0 : this.width.hashCode());
        return result;
    }



    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TextureFindCriteria other = (TextureFindCriteria) obj;

        if (this.height == null) {
            if (other.height != null)
                return false;
        } else if (!this.height.equals(other.height))
            return false;
        if (this.type != other.type)
            return false;
        if (this.subTypeName == null) {
            if (other.subTypeName != null)
                return false;
        } else if (!this.subTypeName.equals(other.subTypeName))
            return false;
        if (this.typeName == null) {
            if (other.typeName != null)
                return false;
        } else if (!this.typeName.equals(other.typeName))
            return false;
        if (this.width == null) {
            if (other.width != null)
                return false;
        } else if (!this.width.equals(other.width))
            return false;
        return true;
    }

    /**
     * @return the colorable
     */
    public boolean isColorable() {
        return colorable;
    }

    /**
     * @param colorable the colorable to set
     */
    public void setColorable(boolean colorable) {
        this.colorable = colorable;
    }

}
