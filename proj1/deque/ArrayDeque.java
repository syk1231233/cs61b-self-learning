package deque;

import afu.org.checkerframework.checker.oigj.qual.O;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayDeque<T> implements Deque, Iterable{
    public T[] items;
    public int first;
    public int last;
    private int size;
    public int length = 8;

    public ArrayDeque(){
        items =(T[]) new Object[length];
        size = 0;
        first = last = 0;
    }

    public void resize(){
        int preLength = length;
        if(size == length) length *= 2;
        else if (size < length / 4 && size >4) length /= 4;
        T[] newItems =(T[]) new Object[length];
        for(int index = first + 1, times = 0; times < size; index++, times++){
           newItems[times] = items[index % preLength];
        }
        items = newItems;
        first = length - 1;
        last = size - 1;
    }
    /**
     * Adds an item of type T to the front of the deque.
     *
     * @param item is never null.
     *
     */
    @Override
    public void addFirst(Object item) {
        items[first] = (T) item;
        first = (first - 1 + length) % length;
        size += 1;
        resize();
    }

    /**
     * Adds an item of type T to the tail of the deque.
     *
     * @param item is never null.
     */
    @Override
    public void addLast(Object item) {
        last = (last + 1) % length;
        items[last] = (T) item;
        size += 1;
        resize();
    }

    /**
     * Return the number of items in the deque.
     *
     * @return the size number.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last,
     * separated by a space.
     * Once all the items have beem printed, print out a new line.
     */
    @Override
    public void printDeque() {
        for(int index = first + 1, times = 0; times < size; index++, times++){
            System.out.print(items[index % length]);
            if(times != (size - 1)) System.out.print(' ');
        }
        System.out.println();
    }

    /**
     * Removes and return the item at the front of the deque.
     * If no such item exists, return null.
     *
     * @return the item at the front of the deque.
     */
    @Override
    public Object removeFirst() {
        if(isEmpty()) return null;
        first = (first + 1) % length;
        Object removeItem = items[first];
        size -= 1;
        resize();
        return removeItem;
    }

    /**
     * Remove and return the item at the tail of the deque.
     * If no such item exists, return null.
     *
     * @return the item at the tail of the deque.
     */
    @Override
    public Object removeLast() {
        if(isEmpty()) return null;
        Object removeItem = items[last];
        last = (last - 1 + length) % length;
        size -= 1;
        resize();
        return removeItem;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item.
     * If no such item exists, return null.
     *
     * @param index The position of the item you want to get.
     * @return The item at the Given index.
     */
    @Override
    public Object get(int index) {
        index = (first + index + 1) % length;
        return items[index];
    }

    public class ArrayDequeIterator implements Iterator{
        public int index;

        public ArrayDequeIterator(){
            index = 0;
        }
        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return index != size;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public Object next() {
            if(hasNext()){
                index += 1;
                return get(index-1);
            }
            else throw new NoSuchElementException("The iteration has no more elements");
        }
    }
    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public boolean equals(Object o){
        if(o == this) return true;
        if(o == null || !(o instanceof java.util.Deque)) return false;
        Deque<T> compareArray = (Deque) o;
        if(compareArray.size() != size) return false;
        for(int i = 0; i < size; i++){
            if(!(compareArray.get(i).equals(this.get(i)))) return false;
        }
        return true;
    }
}
