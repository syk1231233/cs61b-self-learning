package hashmap;

import org.eclipse.jetty.io.ByteBufferPool;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author syk
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    private int getIndex(K key){
        return (key.hashCode() & 0x7fffffff) % M;
    }
    /**
     * Removes all of the mappings from this map.
     */
    @Override
    public void clear() {
        keySet.clear();
        N = 0;
        table = createTable(M);
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key
     */
    @Override
    public boolean containsKey(K key) {
        return keySet.contains(key);
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     *
     * @param key
     */
    @Override
    public V get(K key) {
        if (containsKey(key)){
            Collection<Node> buckets = table[getIndex(key)];
            for(Node node : buckets) if(node.key.equals(key)) return node.value;
        }
        return null;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    @Override
    public int size() {
        return N;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     *
     * @param key
     * @param value
     */
    @Override
    public void put(K key, V value) {
        if((N / (double)M) > LOADFACTOR){
            Collection[] newTable = createTable(M * 2);
            for(K singleKey : keySet){
                V v = get(singleKey);
                int index = (singleKey.hashCode() & 0x7fffffff) % (M * 2);
                if(newTable[index] == null) newTable[index] = createBucket();
                newTable[index].add(createNode(key,value));
            }
            M *= 2;
            table = newTable;
        }
        int index = getIndex(key);
        if(table[index] == null) table[index] = createBucket();
        Collection<Node> bucket = table[index];
        if(!containsKey(key)) {
            bucket.add(createNode(key,value));
            N += 1;
        }
        else{
            for(Node node : bucket){
                if(node.key.equals(key)){
                    bucket.remove(node);
                    break;
                }
            }
            bucket.add(createNode(key,value));
        }
        keySet.add(key);
    }
    /**
     * Returns a Set view of the keys contained in this map.
     */
    @Override
    public Set<K> keySet() {
        return keySet;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     *
     * @param key
     */
    @Override
    public V remove(K key) {
        if(containsKey(key)){
            Collection<Node> bucket = table[getIndex(key)];
            for(Node node : bucket){
                if(node.key.equals(key)){
                    V re = node.value;
                    bucket.remove(node);
                    N -= 1;
                    keySet.remove(key);
                    return re;
                }
            }
        }
        return null;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     *
     * @param key
     * @param value
     */
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<K> iterator() {
        return keySet.iterator();
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private static final int INITIALSIZE = 16;
    private double LOADFACTOR = 0.75;
    private HashSet<K> keySet = new HashSet<>();
    private int M = INITIALSIZE;
    private int N = 0;
    private Collection<Node>[] buckets;
    private Collection[] table;

    /** Constructors */
    public MyHashMap() {
        table = createTable(M);
    }

    public MyHashMap(int initialSize) {
        M = initialSize;
        table = createTable(M);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        M = initialSize;
        LOADFACTOR = maxLoad;
        table = createTable(M);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

}
