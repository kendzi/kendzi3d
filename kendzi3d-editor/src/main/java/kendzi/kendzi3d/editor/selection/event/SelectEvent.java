package kendzi.kendzi3d.editor.selection.event;

/**
 * Selection event for editor object.
 *
 */
public class SelectEvent {

    private boolean selected;

    private int x;

    private int y;

    public SelectEvent(boolean selected, int x, int y) {
        super();
        this.selected = selected;
        this.x = x;
        this.y = y;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
