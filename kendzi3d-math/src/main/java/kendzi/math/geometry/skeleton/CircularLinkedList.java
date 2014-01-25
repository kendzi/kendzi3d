/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.math.geometry.skeleton;

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * @author Kendzi
 * 
 * @param <E>
 */
public class CircularLinkedList<E extends CircularLinkedList.Entry> extends AbstractSequentialList<E> {

    private E header = null; // new E(null, null, null);
    private int size = 0;

    @Override
    public int hashCode() {
        return (header != null ? header.hashCode() : 0) + size;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Constructs an empty list.
     */
    public CircularLinkedList() {
        // this.header.next = this.header.previous = this.header;
        this.header = null;
    }

    /**
     * Returns the first element in this list.
     * 
     * @return the first element in this list
     * @throws NoSuchElementException
     *             if this list is empty
     */
    public E getFirst() {
        if (this.size == 0) {
            return null;
        }
        return this.header;
    }

    /**
     * Returns the last element in this list.
     * 
     * @return the last element in this list
     * @throws NoSuchElementException
     *             if this list is empty
     */
    public E getLast() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }

        return (E) this.header.previous;
    }

    /**
     * Returns the number of elements in this list.
     * 
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Appends the specified element to the end of this list.
     * 
     * <p>
     * This method is equivalent to {@link #addLast}.
     * 
     * @param e
     *            element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    @Override
    public boolean add(E e) {
        addBefore(e, this.header);
        return true;
    }

    // /**
    // * Appends the specified element to the end of this list.
    // *
    // * <p>
    // * This method is equivalent to {@link #addLast}.
    // *
    // * @param e
    // * element to be appended to this list
    // * @return <tt>true</tt> (as specified by {@link Collection#add})
    // */
    // @Override
    // public boolean addBefore(E en) {
    // E e = new E();
    // addBefore(e, en);
    // return true;
    // }

    /**
     * Removes the first occurrence of the specified element from this list, if
     * it is present. If this list does not contain the element, it is
     * unchanged. More formally, removes the element with the lowest index
     * <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists). Returns <tt>true</tt> if this list contained
     * the specified element (or equivalently, if this list changed as a result
     * of the call).
     * 
     * @param pObject
     *            element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
    @Override
    public boolean remove(Object pObject) {
        if (pObject != null) {
            if (this.header == null) {
                return false;
            }
            if (this.header.equals(pObject)) {

                remove((E) pObject);

                // if (this.size == 0) {
                // this.header = null;
                // }
                return true;
            }

            for (E e = (E) this.header.next; e != this.header; e = (E) e.next) {
                if (pObject.equals(e)) {
                    remove(e);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the indexed entry.
     */
    private E entry(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }
        E e = this.header;
        if (index < this.size >> 1) {
            for (int i = 0; i <= index; i++) {
                e = (E) e.next;
            }
        } else {
            for (int i = this.size; i > index; i--) {
                e = (E) e.previous;
            }
        }
        return e;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).
     * 
     * @param index
     *            index at which the specified element is to be inserted
     * @param element
     *            element to be inserted
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        addBefore(element, index == this.size ? this.header : entry(index));
    }

    /**
     * Removes the element at the specified position in this list. Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     * 
     * @param index
     *            the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        return remove(entry(index));
    }

    public E addBefore(E e, E current) {
        if (this.size == 0) {
            this.header = e;
            this.header.next = this.header;
            this.header.previous = this.header;

            this.size++;

            return this.header;
        }
        E newEntry = e; // new E(e, current, current.previous);

        newEntry.next = current;
        newEntry.previous = current.previous;

        newEntry.previous.next = newEntry;
        newEntry.next.previous = newEntry;
        this.size++;
        this.modCount++;
        return newEntry;

    }

    private E remove(E e) {
        if (this.header == null) {
            return null;
        }
        if (e == this.header) {
            if (this.size > 1) {
                this.header = (E) this.header.next;
            } else {
                this.header = null;
            }
        }

        E result = e;
        e.previous.next = e.next;
        e.next.previous = e.previous;

        // e.next = e.previous = null;
        // e.element = null;
        this.size--;
        this.modCount++;
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.util.AbstractSequentialList#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        // TODO Auto-generated method stub
        return super.iterator();
    }

    /*
     * (non-Javadoc)
     * @see java.util.LinkedList#listIterator(int)
     */

    public CircularLinkedListItrerator cirkularListIterator(int index) {
        return new CircularLinkedListItrerator(index);
    }

    //
    // public class CircularLinkedListItrerator2 extends
    // CircularLinkedListItrerator<E> {
    //
    // CircularLinkedListItrerator2(CircularLinkedList<E> circularLinkedList,
    // int index) {
    // circularLinkedList.super(index);
    // }
    //
    //
    // }

    public class CircularLinkedListItrerator implements ListIterator<E> {
        private E lastReturned = CircularLinkedList.this.header;
        private E current;
        private int nextIndex;
        private int expectedModCount = CircularLinkedList.this.modCount;

        CircularLinkedListItrerator(int index) {

            if (index < 0 || index > CircularLinkedList.this.size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + CircularLinkedList.this.size);
            }
            if (index < CircularLinkedList.this.size >> 1) {
                this.current = CircularLinkedList.this.header;
                for (this.nextIndex = 0; this.nextIndex < index; this.nextIndex++) {
                    this.current = (E) this.current.next;
                }
            } else {
                this.current = CircularLinkedList.this.header;
                for (this.nextIndex = CircularLinkedList.this.size; this.nextIndex > index; this.nextIndex--) {
                    this.current = (E) this.current.previous;
                }
            }

            this.current = (E) this.current.previous;
        }

        @Override
        public boolean hasNext() {
            return CircularLinkedList.this.size != 0;
        }

        @Override
        public E next() {
            checkForComodification();

            if (this.nextIndex == CircularLinkedList.this.size) {
                this.nextIndex = 0;
            }

            this.nextIndex++;
            this.lastReturned = this.current;
            this.current = (E) this.current.next;
            return this.current;
        }

        @Override
        public boolean hasPrevious() {
            return CircularLinkedList.this.size != 0;
        }

        @Override
        public E previous() {
            checkForComodification();

            if (this.nextIndex == 0) {
                this.nextIndex = CircularLinkedList.this.size;

            }

            this.lastReturned = this.current;

            this.current = (E) this.current.previous;
            this.nextIndex--;
            return this.current;

        }

        public E current() {
            return this.current;
        }

        @Override
        public int nextIndex() {
            return this.nextIndex;
        }

        @Override
        public int previousIndex() {
            return this.nextIndex - 1;
        }

        @Override
        public void remove() {
            checkForComodification();
            E lastNext = (E) this.current.next;
            try {
                CircularLinkedList.this.remove(this.current);
            } catch (NoSuchElementException e) {
                throw new IllegalStateException();
            }

            // if (this.current == this.lastReturned) {
            this.current = lastNext;
            // } else {
            this.nextIndex--;
            // }
            // this.lastReturned = CircularLinkedList.this.header;
            this.expectedModCount++;
        }

        // @Override
        // public void set(E e) {
        // if (this.lastReturned == CircularLinkedList.this.header)
        // throw new IllegalStateException();
        // checkForComodification();
        // this.lastReturned.element = e;
        // }

        @Override
        public void add(E e) {
            checkForComodification();
            this.lastReturned = CircularLinkedList.this.header;
            CircularLinkedList.this.addBefore(e, this.current);
            this.nextIndex++;
            this.expectedModCount++;
        }

        final void checkForComodification() {
            if (CircularLinkedList.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        public E getPrevious() {
            return (E) this.current.previous;
        }

        public E getNext() {
            return (E) this.current.next;
        }

        @Override
        public void set(E e) {
            // TODO Auto-generated method stub

        }
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    public static class Entry {
        Object parent;

        // E element;
        public Entry next;
        public Entry previous;

        public Entry() {
            this.next = null;
            this.previous = null;
        }

        Entry(Entry next, Entry previous) {
            // this.element = element;
            this.next = next;
            this.previous = previous;
        }

        Entry next() {
            return this.next;
        }

        Entry previous() {
            return this.previous;
        }
    }

}
