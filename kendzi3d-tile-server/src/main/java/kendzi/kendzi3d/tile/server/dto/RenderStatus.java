package kendzi.kendzi3d.tile.server.dto;

import java.util.Date;

import kendzi.kendzi3d.render.tile.Tile;

public class RenderStatus {
    private Date date;
    private boolean succes;
    private boolean catche;
    private double time;
    private Tile tile;



    public RenderStatus(Date date, boolean succes, boolean catche, double time, Tile tile) {
        super();
        this.date = date;
        this.succes = succes;
        this.catche = catche;
        this.time = time;
        this.tile = tile;
    }
    /**
     * @return the succes
     */
    public boolean isSucces() {
        return succes;
    }
    /**
     * @param succes the succes to set
     */
    public void setSucces(boolean succes) {
        this.succes = succes;
    }
    /**
     * @return the catche
     */
    public boolean isCatche() {
        return catche;
    }
    /**
     * @param catche the catche to set
     */
    public void setCatche(boolean catche) {
        this.catche = catche;
    }
    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }
    /**
     * @param time the time to set
     */
    public void setTime(double time) {
        this.time = time;
    }
    /**
     * @return the tile
     */
    public Tile getTile() {
        return tile;
    }
    /**
     * @param tile the tile to set
     */
    public void setTile(Tile tile) {
        this.tile = tile;
    }
    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }
    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

}
