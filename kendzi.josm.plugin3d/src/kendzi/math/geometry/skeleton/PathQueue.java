package kendzi.math.geometry.skeleton;

import java.util.Iterator;


public class PathQueue<T extends PathQueueNode> implements Iterable<T> {
    int size = 0;
    T first = null;



    public void addPush(T node, T newNode) {
        if (newNode.list != null) {
            throw new RuntimeException("Node is allready assigned to different list!");
        }

        if (node.next != null && node.previous != null) {
            throw new RuntimeException("Can't push new node. Node is inside a Quere. New node can by added only at the end of queue.");
        }

        newNode.list = this;
        this.size++;

        if (node.next == null) {

            newNode.previous = node;
            newNode.next = null;

            node.next = newNode;

        } else {

            newNode.previous = null;
            newNode.next = node;

            node.previous = newNode;
        }
    }

    public int size() {
        return this.size;
    }

    public void addFirst(T node) {
        if (node.list != null) {
            throw new RuntimeException("Node is allready assigned to different list!");
        }

        if (this.first == null) {
            this.first = node;

            node.list = this;
            node.next = null;
            node.previous = null;

            this.size++;

        } else {

            throw new RuntimeException("First element is exist!");
        }

    }


    public T pop(T node) {
      if (node.list != this) {
          throw new RuntimeException("Node is not assigned to this list!");
      }

      if (this.size <= 0) {
          throw new RuntimeException("List is empty can't remove!");
      }
      if (!node.isEnd()) {
          throw new RuntimeException("Can pop only from end of queue!");
      }

      node.list = null;

      T previous = null;

      if (this.size == 1) {
          this.first = null;

      } else {

          if (this.first == node) {
              if (node.next !=null) {
                  this.first = (T) node.next;
              } else if (node.previous !=null) {
                  this.first = (T) node.previous;
              } else {
                  throw new RuntimeException("Ups ?");
              }

          }
          if (node.next !=null) {
              node.next.previous = null;
              previous = (T) node.next;
          } else if (node.previous !=null) {
              node.previous.next  = null;
              previous = (T) node.previous;
          }

//          node.previous.next = node.next;
//          node.next.previous = node.previous;
      }

      // XXX
      node.previous = null;
      node.next = null;

      this.size--;

      return previous;
  }


//    public void remove(T node) {
//        if (node.list != this) {
//            throw new RuntimeException("Node is not assigned to this list!");
//        }
//
//        if (this.size <= 0) {
//            throw new RuntimeException("List is empty can't remove!");
//        }
//
//        node.list = null;
//
//        if (this.size == 1) {
//            this.first = null;
//
//        } else {
//
//            if (this.first == node) {
//                this.first = (T) this.first.next;
//            }
//
//            node.previous.next = node.next;
//            node.next.previous = node.previous;
//        }
//
//        // XXX
//        node.previous = null;
//        node.next = null;
//
//        this.size--;
//    }

    public T first() {
        return this.first;
    }



    public class PathQueueIterator implements Iterator<T> {

        int i = 0;

        T current = (T) (PathQueue.this.first != null ? PathQueue.this.first.findEnd() : null);


        @Override
        public boolean hasNext() {
            return this.i < PathQueue.this.size;
        }

        @Override
        public T next() {
            T ret = this.current;
            this.current = (T) this.current.next;
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
//    /**
//     * Make one round around list. Start from first element end on last.
//     * @return interator
//     */
    @Override
    public Iterator<T> iterator() {
        return new PathQueueIterator();
    }

}

