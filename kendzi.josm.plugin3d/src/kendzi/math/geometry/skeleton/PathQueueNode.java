package kendzi.math.geometry.skeleton;

public class PathQueueNode {

    PathQueueNode next;
    PathQueueNode previous;

    protected PathQueue list;

    public PathQueue list() {
        return this.list;
    }

//
//    public PathQueueNode next() {
//        return this.next;
//    }
//
//    public PathQueueNode previous() {
//        return this.previous;
//    }

    public void addPush(PathQueueNode node) {
        this.list.addPush(this, node);
    }

    public boolean isEnd() {
        return this.next == null || this.previous == null;
    }

    public PathQueueNode next(PathQueueNode pPrevious) {
        if (pPrevious == null || pPrevious == this) {
            if (!isEnd()) {
                throw new RuntimeException("Can't get next element don't knowing previous one. Directon is unknown");
            } else
            if (this.next == null) {
                return this.next;
            } else if (this.previous == null) {
                return this.previous;
            } else {
                return null;
            }
        }

        if (this.next == pPrevious) {
            return this.previous;
        } else {
            return this.next;
        }
    }

    public PathQueueNode prevoius() {
        if (!isEnd()) {
            throw new RuntimeException("Can get previous only from end of queue");
        }

        if (this.next == null) {
            return this.previous;
        } else if (this.previous == null) {
            return this.next;
        } else {

        }
        return null;
    }

    public PathQueueNode addQueue(PathQueueNode queue) {

        if (this.list == queue.list) {
            // TODO ? cycle ?!
            return null;
        }

        PathQueueNode currentQueue = this;

        PathQueueNode previous = prevoius();
        PathQueueNode current = queue;
        PathQueueNode next = null;
        while (current != null) {

            next = current.pop();
            currentQueue.addPush(current);
            currentQueue = current;


            current = next;
        }

        return currentQueue;

    }


    public PathQueueNode findEnd() {
        if (isEnd()) {
            return this;
        }

        PathQueueNode current = this;
        while (current.previous != null) {
            current = current.previous;
        }
        return current;

    }

    public PathQueueNode pop() {
        return this.list.pop(this);
    }
}
