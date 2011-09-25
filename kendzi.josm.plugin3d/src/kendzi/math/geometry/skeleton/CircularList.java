package kendzi.math.geometry.skeleton;

import java.util.Iterator;

public class CircularList<T extends CircularNode> implements Iterable<T> {
    int size = 0;
    T first = null;



    public void addNext(T node, T newNode) {
        if (newNode.list != null) {
            throw new RuntimeException("Node is allready assigned to different list!");
        }
        newNode.list = this;

        newNode.previous = node;
        newNode.next = node.next;

        node.next.previous = newNode;
        node.next = newNode;

        this.size++;
    }

    public void addPrevious(T node, T newNode) {
        if (newNode.list != null) {
            throw new RuntimeException("Node is allready assigned to different list!");
        }
        newNode.list = this;

        newNode.previous = node.previous;
        newNode.next = node;

        node.previous.next = newNode;
        node.previous = newNode;

        this.size++;
    }

    public int size() {
        return this.size;
    }

    public void addLast(T node) {
        if (node.list != null) {
            throw new RuntimeException("Node is allready assigned to different list!");
        }

        if (this.first == null) {
            this.first = node;

            node.list = this;
            node.next = node;
            node.previous = node;

            this.size++;
        } else {

            addPrevious(this.first, node);
        }

    }

    public void remove(T node) {
        if (node.list != this) {
            throw new RuntimeException("Node is not assigned to this list!");
        }

        if (this.size <= 0) {
            throw new RuntimeException("List is empty can't remove!");
        }

        node.list = null;

        if (this.size == 1) {
            this.first = null;

        } else {

            if (this.first == node) {
                this.first = (T) this.first.next;
            }

            node.previous.next = node.next;
            node.next.previous = node.previous;
        }

        // XXX
        node.previous = null;
        node.next = null;

        this.size--;
    }

    public T first() {
        return this.first;
    }



    public class CircularListIterator implements Iterator<T> {

        int i = 0;

        T current = CircularList.this.first;


        @Override
        public boolean hasNext() {
            return this.i < CircularList.this.size;
        }

        @Override
        public T next() {
            T ret = this.current;
            this.current = (T) this.current.next();
            this.i++;
            return ret;
        }

        @Override
        public void remove() {
            throw new RuntimeException("TODO");
        }

    }

//    /**
//     * Make one round around list. Start from first element end on last.
//     * @return interator
//     */
//    public CircularListIterator roundIterator() {
//        return new CircularListIterator();
//    }
    /**
     * Make one round around list. Start from first element end on last.
     * @return interator
     */
    @Override
    public Iterator<T> iterator() {
        return new CircularListIterator();
    }

}

