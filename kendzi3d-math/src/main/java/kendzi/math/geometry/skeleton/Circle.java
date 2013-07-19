/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.skeleton;

import java.util.ListIterator;
import java.util.NoSuchElementException;


public class Circle<E> {
    private int size;

    private Entry<E> header = new Entry<E>(null, null, null);


    /**
     * Returns the first element in this list.
     *
     * @return the first element in this list
     * @throws NoSuchElementException
     *             if this list is empty
     */
    public E getFirst() {
        if (size == 0)
            throw new NoSuchElementException();

        return header.next.element;
    }


    private static class Entry<E> {
        E element;
        Entry<E> next;
        Entry<E> previous;

        Entry(E element, Entry<E> next, Entry<E> previous) {
            this.element = element;
            this.next = next;
            this.previous = previous;
        }
    }



    private class ListItr implements ListIterator<E> {

        @Override
        public boolean hasNext() {

            return size !=0;
        }

        @Override
        public E next() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return size !=0;
        }

        @Override
        public E previous() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int nextIndex() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int previousIndex() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void remove() {
            // TODO Auto-generated method stub

        }

        @Override
        public void set(E e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void add(E e) {
            // TODO Auto-generated method stub

        }

    }

}
