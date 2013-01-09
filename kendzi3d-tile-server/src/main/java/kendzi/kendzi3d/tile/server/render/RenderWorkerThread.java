package kendzi.kendzi3d.tile.server.render;

import kendzi.kendzi3d.render.RenderResult;
import kendzi.kendzi3d.tile.server.render.job.ExitJob;
import kendzi.kendzi3d.tile.server.render.job.RenderJob;
import kendzi.kendzi3d.tile.server.render.job.TileJob;
import kendzi.kendzi3d.tile.server.render.worker.TitleJobRender;

import org.apache.log4j.Logger;

public class RenderWorkerThread extends Thread {

    /** Log. */
    private static final Logger log = Logger.getLogger(RenderWorkerThread.class);

    WorkPoll workPoll;

    TitleJobRender titleJobRender;

    private boolean run = true;

    public RenderWorkerThread(WorkPoll workPoll, TitleJobRender titleRender) {
        super();
        this.workPoll = workPoll;
        this.titleJobRender = titleRender;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        this.titleJobRender.init();
        while (this.run) {
            try {
                RenderJobStatus sj = takeJob();

                RenderJob renderJob = sj.getRenderJob();

                RenderResult jobResult = null;
                try {

                    if (renderJob instanceof ExitJob) {
                        break;
                    }

                    jobResult = renderJob(renderJob);

                } catch (Exception e) {
                    log.error("Error rendering job: " + renderJob, e);
                } finally {
                    // mark as executed, not important if it is error
                    sj.execute(jobResult);
                    synchronized (this.workPoll) {
                        this.workPoll.notifyAll();
                    }
                    //                this.notifyAll();
                }
            } catch (Exception e) {
                log.error("error taking job", e);
            }
        }
        this.titleJobRender.release();
    }

    //    @Override
    //    synchronized void notifyAll() {
    //        notifyAll()
    //    }

    private RenderResult renderJob(RenderJob renderJob) {

        if (renderJob instanceof TileJob) {
            return this.titleJobRender.render((TileJob)renderJob);
        } else {
            log.error("unknown render job: " + renderJob);
        }
        return null;
    }

    private RenderJobStatus takeJob() {
        return this.workPoll.takeOrWait();
    }

}
