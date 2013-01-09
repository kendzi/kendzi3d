package kendzi.kendzi3d.tile.server.render;

import kendzi.kendzi3d.render.RenderResult;
import kendzi.kendzi3d.tile.server.render.job.RenderJob;


public class RenderJobStatus {

    private boolean ready;

    private RenderJob renderJob;

    private RenderResult jobResult;



    public RenderJobStatus(RenderJob renderJob) {
        this.renderJob = renderJob;
        this.ready = false;
    }

    public RenderJob getRenderJob() {
        return renderJob;
    }

    public synchronized boolean isReady() {
        return this.ready;
    }

    public synchronized void execute(RenderResult jobResult) {

        this.jobResult= jobResult;
        this.ready = true;
    }

    /**
     * @return the jobResult
     */
    public synchronized RenderResult getJobResult() {
        return jobResult;
    }
}