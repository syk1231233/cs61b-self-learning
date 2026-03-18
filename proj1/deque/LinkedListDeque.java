package deque;

import com.google.gson.internal.ObjectConstructor;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Deque<T>,Iterable<T>{
    private int size;
    public Item<T> sentry;

    public class LinkedListDequeIterator implements Iterator{
        private Item<T> curNode;

        public LinkedListDequeIterator(){
            curNode = sentry.pre;
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
            return curNode != sentry;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public Object next(){
            if(hasNext()){
                Object value = curNode.value;
                curNode = curNode.next;
                return value;
            }else{
                throw new NoSuchElementException("The iteration has no more elements");
            }
        }
    }
    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    public static class Item<E>{
        public Item<E> pre;
        public Item<E> next;
        E value;

        public Item(E val){
            pre = null;
            next = null;
            value = val;
        }
    }
    /**
     * Init a new LinkedListDeque object.
     */
    public LinkedListDeque(){
        size = 0;
        sentry = new Item<>(null);
        sentry.pre = sentry;
        sentry.next = sentry;
    }
    /**
     * Adds an item of type T to the front of the deque.
     *
     * @param item is never null.
     *
     */
    @Override
    public void addFirst(T item) {
        Item<T> addNode = new Item<>(item);
        if(size == 0){
            sentry.pre = addNode;
            addNode.pre = sentry;
            sentry.next = addNode;
            addNode.next = sentry;
        }else{
            addNode.next = sentry.pre;
            addNode.next.pre = addNode;
            sentry.pre = addNode;
            addNode.pre = sentry;
        }
        size += 1;
    }

    /**
     * Adds an item of type T to the tail of the deque.
     *
     * @param item is never null.
     */
    @Override
    public void addLast(T item) {
        Item<T> addNode = new Item<>(item);
        if(size == 0){
            sentry.pre = addNode;
            sentry.next = addNode;
            addNode.next = sentry;
            addNode.pre = sentry;
        }
        else{
            addNode.pre = sentry.next;
            sentry.next.next = addNode;
            addNode.next = sentry;
            sentry.next = addNode;
        }
        size += 1;
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
        Item<T> curNode = sentry.pre;
        while(curNode != sentry){
            System.out.print(curNode.value);
            if(curNode != sentry.next) System.out.print(' ');
            curNode = curNode.next;
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
    public T removeFirst() {
        if(size == 0) return null;
        Item<T> removeNode = sentry.pre;
        sentry.pre = removeNode.next;
        sentry.pre.pre = sentry;
        removeNode.pre = removeNode.next = null;
        size -= 1;
        if(size == 0) sentry.next = sentry;
        return removeNode.value;
    }

    /**
     * Remove and return the item at the tail of the deque.
     * If no such item exists, return null.
     *
     * @return the item at the tail of the deque.
     */
    @Override
    public T removeLast() {
        if(size == 0) return null;
        Item<T> removeNode = sentry.next;
        sentry.next = removeNode.pre;
        sentry.next.next = sentry;
        removeNode.pre = removeNode.next = null;
        size -= 1;
        if(size == 0) sentry.pre = sentry;
        return removeNode.value;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item.
     * If no such item exists, return null.
     *
     * @param index The position of the item you want to get.
     * @return The item at the given index.
     */
    @Override
    public T get(int index) {
        if(index > size || index < 0 || isEmpty()) return null;
        for(T curNodeValue : this){
            if(index == 0) return curNodeValue;
            index -= 1;
        }
        return null;
    }

    public T getRecursiveHelp(int index, Item<T> curNode,boolean ispre){
        if(index == 0) return curNode.value;
        if(ispre) return getRecursiveHelp(index - 1, curNode.pre, true);
        else return getRecursiveHelp(index - 1, curNode.next, false);
    }
    /**
     * Get method's other version, making by recursive.
     * @param index The position of the item you want to get.
     * @return The item at the given index.
     */
    public T getRecursive(int index){
        if(index > size || index < 0) return null;
        if(index > size - index) return getRecursiveHelp(size - 1 - index, sentry.next, true);
        else return getRecursiveHelp(index, sentry.pre, false);
    }

    @Override
    /**
     * Return whether or not the parameter o is equal to the deque.
     * If equal, o must be a Deque and if it contains the same contents in the same order.
     */
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || !(o instanceof Deque)) return false;
        Deque<T> compareArray = (Deque) o;
        if(compareArray.size() != size) return false;
        for(int i = 0; i < size; i++){
            if(!(compareArray.get(i) .equals(this.get(i)))) return false;
        }
        return true;
    }
}
