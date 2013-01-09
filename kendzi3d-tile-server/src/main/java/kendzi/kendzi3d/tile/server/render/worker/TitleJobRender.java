package kendzi.kendzi3d.tile.server.render.worker;

import kendzi.kendzi3d.render.RenderResult;
import kendzi.kendzi3d.tile.server.render.job.TileJob;

public interface TitleJobRender {

    /**
     * Init of render worker.
     */
    void init();

    /**
     *  Render job for tile.
     * @param tj tile job
     * @return rendered tile
     */
    RenderResult render(TileJob tj);

    /**
     * Release of render worker.
     */
    void release();
}

