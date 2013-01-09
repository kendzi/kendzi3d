package kendzi.kendzi3d.tile.server.render.worker.impl;

import kendzi.kendzi3d.render.ImageRender;
import kendzi.kendzi3d.render.RenderResult;
import kendzi.kendzi3d.render.conf.RenderEngineConf;
import kendzi.kendzi3d.render.dataset.DataSetProvider;
import kendzi.kendzi3d.tile.server.render.job.TileJob;
import kendzi.kendzi3d.tile.server.render.worker.TitleJobRender;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class Kendzi3dTileRenderWorker implements TitleJobRender {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Kendzi3dTileRenderWorker.class);

    @Inject
    ImageRender imageRender;

    @Inject
    RenderEngineConf conf;

    @Inject
    DataSetProvider dataSource;


    @Override
    public void init() {
        this.imageRender.init();
    }

    @Override
    public RenderResult render(TileJob t) {

        return this.imageRender.render(t);
    }

    @Override
    public void release() {
        this.imageRender.release();
    }

}

