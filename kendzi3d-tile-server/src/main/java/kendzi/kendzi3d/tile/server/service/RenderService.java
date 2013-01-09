package kendzi.kendzi3d.tile.server.service;

import java.util.Date;

import javax.annotation.PostConstruct;

import kendzi.kendzi3d.render.RenderResult;
import kendzi.kendzi3d.render.conf.RenderDataSourceConf;
import kendzi.kendzi3d.render.conf.RenderEngineConf;
import kendzi.kendzi3d.render.dataset.DataSetProvider;
import kendzi.kendzi3d.render.dataset.DataSetProviderFactory;
import kendzi.kendzi3d.render.tile.Tile;
import kendzi.kendzi3d.tile.server.dto.RenderStatus;
import kendzi.kendzi3d.tile.server.render.RenderWorkerThread;
import kendzi.kendzi3d.tile.server.render.WorkPoll;
import kendzi.kendzi3d.tile.server.render.job.TileJob;
import kendzi.kendzi3d.tile.server.render.module.RenderModule;
import kendzi.kendzi3d.tile.server.render.worker.impl.Kendzi3dTileRenderWorker;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Service
public class RenderService {

    @Autowired
    Environment environment;

    //    @Autowired
    //    DataSource dataSource;

    @Autowired
    RenderEngineConf renderEngineConf;

    @Autowired
    RenderDataSourceConf renderDataSourceConf;

    @Autowired
    RenderStatusService renderStatusService;



    /** Log. */
    private static final Logger log = Logger.getLogger(RenderService.class);

    private Injector injector;

    private WorkPoll workPoll;

    private RenderWorkerThread renderThread;



    @PostConstruct
    public void init() {
        try {
            DataSetProvider dsp = DataSetProviderFactory.loadConf(renderDataSourceConf);

            this.injector = Guice.createInjector(new RenderModule(this.renderEngineConf, dsp));

            this.workPoll = new WorkPoll();

            Kendzi3dTileRenderWorker k3dTileRenderer = this.injector.getInstance(Kendzi3dTileRenderWorker.class);

            this.renderThread = new RenderWorkerThread(this.workPoll, k3dTileRenderer);

            log.info("starting render thread begin");
            this.renderThread.start();
            log.info("starting render thread end");

        } catch (Exception e) {
            throw new RuntimeException("error initing renderer service", e);
        }
    }

    public byte[] render(Tile tile) {
        Date date = new Date();
        long s1 = System.currentTimeMillis();

        log.info("adding job");

        RenderResult jobResult =  this.workPoll.putJobAndWaitForResult(new TileJob(tile.getX(), tile.getY(), tile.getZ(), ""));

        log.info("job executed");

        double time = (System.currentTimeMillis() - s1) / 1000d;

        log.info("Tile process time: " + time);

        this.renderStatusService.save(
                new RenderStatus(
                        date,
                        jobResult.getImage() != null,
                        false,
                        time,
                        tile));

        return jobResult != null ? jobResult.getImage() : null;
    }
}
