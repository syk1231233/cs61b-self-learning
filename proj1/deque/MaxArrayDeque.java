package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private final Comparator<T> isBigger;
    /** Creates a MaxArrayDeque with the given Comparator.
     *
     * @param c is a function which receives the parameter we want to compare and returns the result.
     */
    public MaxArrayDeque(Comparator<T> c){
        isBigger = c;
    }

    /**
     * Return the maximum element in the deque as governed bt the previously give Comparator.
     * If the MaxArrayDeque is empty, return null.
     * @return the maximum element.
     */
    public T max(){
        if(isEmpty()) return null;
        T maxValue = (T) this.get(0);
        Iterator<T> iterator = new ArrayDequeIterator();
        iterator.next();
        while(iterator.hasNext()){
            T challenger = iterator.next();
            if(isBigger.compare(maxValue,challenger) < 0){
                maxValue = challenger;
            }
        }
        return maxValue;
    }

    /**
     * Return the maximum value in MaxArrayDeque governed by the parameter c.
     * If the MaxArrayDeque is empty, return the null.
     * @param c govern the compare method.
     * @return the maximum value in MaxArrayDeque.
     */
    public T max(Comparator<T> c){
        if(isEmpty()) return null;
        T maxValue = (T) this.get(0);
        Iterator<T> iterator = new ArrayDequeIterator();
        iterator.next();
        while(iterator.hasNext()){
            T challenger = iterator.next();
            if(c.compare(maxValue,challenger) < 0){
                maxValue = challenger;
            }
        }
        return maxValue;
    }
}
