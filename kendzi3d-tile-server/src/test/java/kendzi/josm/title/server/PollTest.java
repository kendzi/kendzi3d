package kendzi.josm.title.server;

import kendzi.kendzi3d.render.conf.RenderEngineConf;
import kendzi.kendzi3d.render.module.RenderModule;
import kendzi.kendzi3d.tile.server.render.RenderWorkerThread;
import kendzi.kendzi3d.tile.server.render.WorkPoll;
import kendzi.kendzi3d.tile.server.render.job.ExitJob;
import kendzi.kendzi3d.tile.server.render.job.TileJob;

import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class PollTest {
    /** Log. */
    private static final Logger log = Logger.getLogger(PollTest.class);


    public static void main(String[] args) {
        PollTest m = new PollTest();
        m.start();
    }

    private void start() {
        long s1 = System.currentTimeMillis();


        RenderEngineConf k3dconf = new RenderEngineConf();

        k3dconf.setResDir("c:/java/workspace/sun/kendzi.josm.plugin3d");

        Injector injector = Guice.createInjector(new RenderModule(k3dconf, null));




        //        WorkPoll workPoll = new WorkPoll();
        //        TitleJobRenderer titleRender = new Kendzi3dTitleJobRenderer();
        //        RenderThread rt = new RenderThread(workPoll, titleRender);

        RenderWorkerThread rt = injector.getInstance(RenderWorkerThread.class);

        rt.start();

        System.out.println("adding job");

        WorkPoll workPoll = injector.getInstance(WorkPoll.class);

        workPoll.putJobAndWaitForResult(new TileJob(142550, 86423, 18, ""));

        System.out.println("job executed");

        System.out.println("adding exit job");
        workPoll.putJobAndWaitForResult(new ExitJob());
        System.out.println("exit job executed");

        log.info("Tile process time: " + (System.currentTimeMillis() - s1) / 1000d);

    }
}
