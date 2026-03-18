package deque;

/**
 * Double ended queue
 * Deque are sequence containers with dynamic sizes that can be
 * expended or contracted on both ends
 * @author syk1231233
 * @since 2026-03-17
 */
public interface Deque <T>{
    /**
     * Adds an item of type T to the front of the deque.
     * @param item is never null.
     * */
    public void addFirst(T item);

    /**
     * Adds an item of type T to the tail of the deque.
     * @param item is never null.
     */
    public void addLast(T item);

    /**
     * Check if the deque is empty.
     * @return true if deque is empty, false otherwise.
     */
    public default boolean isEmpty(){
        return size() == 0;
    }

    /**
     * Return the number of items in the deque.
     * @return the size number.
     */
    public int size();

    /**
     * Prints the items in the deque from first to last,
     * separated by a space.
     * Once all the items have beem printed, print out a new line.
     */
    public void printDeque();

    /**
     * Removes and return the item at the front of the deque.
     * If no such item exists, return null.
     * @return the item at the front of the deque.
     */
    public T removeFirst();

    /**
     * Remove and return the item at the tail of the deque.
     * If no such item exists, return null.
     * @return the item at the tail of the deque.
     */
    public T removeLast();

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item.
     * If no such item exists, return null.
     * @param index The position of the item you want to get.
     * @return The item at the Given index.
     */
    public T get(int index);
}
