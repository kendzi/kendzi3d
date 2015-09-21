package kendzi.kendzi3d.buildings.model.roof.shape;

public enum DormerRow {
    ROW_1(1, "row1"),
    ROW_2(2, "row2"),
    ROW_3(3, "row3"),
    ROW_4(4, "row4"),
    ROW_5(5, "row5");

    int row;
    String key;

    DormerRow(int pRow, String pKey) {
        this.row = pRow;
        this.key = pKey;
    }

    public int getRowNum() {
        return this.row;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }



}
