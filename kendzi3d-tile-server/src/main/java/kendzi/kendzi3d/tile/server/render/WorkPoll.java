package kendzi.kendzi3d.tile.server.render;

import java.util.ArrayList;

import kendzi.kendzi3d.render.RenderResult;
import kendzi.kendzi3d.tile.server.render.job.RenderJob;

public class WorkPoll {
    // http://docs.oracle.com/javase/tutorial/essential/concurrency/guardmeth.html


    ArrayList<RenderJobStatus> poll = new ArrayList<RenderJobStatus>();

    //    SynchronousQueue<TitleJob> queue = new SynchronousQueue<TitleJob>();
    //    //    BlockingQueue<TitleJob> queue = new BlockingQueue<TitleJob>();
    //    ArrayList<TitleJob> queue = new ArrayList<TitleJob>();
    //
    //    int MAX_QUEUE_SIZE = 5;


    public synchronized RenderResult putJobAndWaitForResult(RenderJob tj) {
        RenderJobStatus sj = new RenderJobStatus(tj);

        addJob(sj);

        // wait for result (generated in different thread!
        while (!sj.isReady()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                //
            }
        }
        return sj.getJobResult();
    }

    private void addJob(RenderJobStatus sj) {

        this.poll.add(sj);

        notifyAll();
    }

    //    private StatusJob takeJob() {
    //        if (this.poll.isEmpty()) {
    //            return null;
    //        }
    //        return this.poll.remove(0);
    //    }

    public synchronized RenderJobStatus takeOrWait() {

        // Wait until message is
        // available.
        while (this.poll.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }

        RenderJobStatus remove = this.poll.remove(0);
        notifyAll();
        return remove;
    }


}
