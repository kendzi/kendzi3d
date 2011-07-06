/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.skeleton;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import kendzi.math.geometry.skeleton.CircularLinkedList.CircularLinkedListItrerator;

import org.junit.Test;

public class CircularLinkedListTest {

    private static class IEntry extends CircularLinkedList.Entry {

        public IEntry(Integer pI) {
            this.i = pI;
        }
        public Integer i;
    }

    @Test
    public void circularAddTest() {
        LinkedList<Integer> dd;
        int[] points = { 1, 2, 3, 4 };

        CircularLinkedList<IEntry> circular = new CircularLinkedList<IEntry>();

        for (int i : points) {
            circular.add(new IEntry(i));
        }

        CircularLinkedListItrerator iterator = circular.cirkularListIterator(0);

        assertEquals(Integer.valueOf(4), ((IEntry) iterator.current()).i);
        assertEquals(Integer.valueOf(1), ((IEntry) iterator.next()).i);
        assertEquals(Integer.valueOf(1), ((IEntry) iterator.current()).i);
        assertEquals(Integer.valueOf(2), ((IEntry) iterator.next()).i);
        assertEquals(Integer.valueOf(2), ((IEntry) iterator.current()).i);
        assertEquals(Integer.valueOf(3), ((IEntry) iterator.next()).i);
        assertEquals(Integer.valueOf(4), ((IEntry) iterator.next()).i);
        assertEquals(Integer.valueOf(1), ((IEntry) iterator.next()).i);

    }

    @Test
    public void circularRemoveTest() {
        LinkedList<Integer> dd;
        int[] points = { 1, 2, 3, 4 };

        CircularLinkedList<IEntry> circular = new CircularLinkedList<IEntry>();

        for (int i : points) {
            circular.add(new IEntry(i));
        }

        CircularLinkedListItrerator iterator = circular.cirkularListIterator(0);

        assertEquals(Integer.valueOf(1), ((IEntry) iterator.next()).i);
        assertEquals(Integer.valueOf(2), ((IEntry) iterator.next()).i);
        assertEquals(Integer.valueOf(3), ((IEntry) iterator.next()).i);

        iterator.remove();

        assertEquals(Integer.valueOf(4), ((IEntry) iterator.current()).i);

        // assertEquals(Integer.valueOf(4), iterator.next());
        assertEquals(Integer.valueOf(1), ((IEntry) iterator.next()).i);
        assertEquals(Integer.valueOf(4), ((IEntry) iterator.previous()).i);
        assertEquals(Integer.valueOf(2), ((IEntry) iterator.previous()).i);

    }

    @Test
    public void circularRemove() {

        int[] points = { 1, 2, 3, 4 };

        CircularLinkedList<IEntry> circular = new CircularLinkedList<IEntry>();

        for (int i : points) {
            circular.add(new IEntry(i));
        }

        IEntry first1 = circular.getFirst();


        assertEquals(Integer.valueOf(1), first1.i);

        circular.remove(first1);

        assertEquals(3, circular.size());
        assertEquals(Integer.valueOf(2), circular.getFirst().i);

        circular.remove(circular.getFirst().next());
        assertEquals(2, circular.size());
        assertEquals(Integer.valueOf(4), ((IEntry) circular.getFirst().next()).i);


        circular.remove(circular.getFirst().previous());
        assertEquals(1, circular.size());
        assertEquals(Integer.valueOf(2), (circular.getFirst()).i);
        assertEquals(Integer.valueOf(2), ((IEntry) circular.getFirst().next()).i);
        assertEquals(Integer.valueOf(2), ((IEntry) circular.getFirst().previous()).i);

        circular.remove( circular.getFirst());
        assertEquals(0, circular.size());
        assertEquals(null, (circular.getFirst()));


    }

}
