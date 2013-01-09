package kendzi.kendzi3d.tile.server.render.job;

import kendzi.kendzi3d.render.tile.Tile;


public class TileJob extends Tile implements RenderJob {

    String profile;

    /**
     * @return the profile
     */
    public String getProfile() {
        return this.profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    public TileJob(int x, int y, int z, String profile) {
        super(x, y, z);

        this.profile = profile;
    }
}
