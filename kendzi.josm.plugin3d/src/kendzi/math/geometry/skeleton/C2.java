/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.skeleton;

import java.util.LinkedList;
import java.util.ListIterator;

public class C2<E> extends LinkedList<E> {


    public ListIterator<E> circleListSimpleIterator(int index) {
        return new ListItr();

    }


    private class ListItr implements ListIterator<E> {

//        private Entry<E> lastReturned = header;
//        private Entry<E> next;


        @Override
        public boolean hasNext() {

            return size() != 0;
        }

        @Override
        public E next() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return size() != 0;
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
