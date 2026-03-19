package bstmap;

import afu.org.checkerframework.checker.oigj.qual.O;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @param <K> implements Comparable and have a compareTo method.
 * @param <V>
 */
public class BSTMap<K extends Comparable,V> implements Map61B{
    public BSTNode root;
    public int size;

    private class BSTNode{
        public K nodeKey;
        public V nodeValue;
        BSTNode left;
        BSTNode right;

        public BSTNode(K key, V value){
            nodeKey = key;
            nodeValue = value;
            left = null;
            right = null;
        }

    }
    public BSTMap(){
        size = 0;
        root = null;
    }
    /**
     * Removes all of the mappings from this map.
     */
    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    public BSTNode lookthroughHelper(BSTNode curNode, K key){
        if(curNode == null) return null;
        int result = curNode.nodeKey.compareTo(key);
        if(result == 0) return curNode;
        else if (result < 0) {
            return lookthroughHelper(curNode.right,key);
        }else return lookthroughHelper(curNode.left,key);
    }
    @Override
    public boolean containsKey(Object key) {
        if(size == 0 || root == null) return false;
        return lookthroughHelper(root,(K)key) != null;
    }

    @Override
    public Object get(Object key) {
        if (size == 0 || !containsKey(key)) return null;
        return lookthroughHelper(root,(K)key).nodeValue;
    }

    @Override
    public int size() {
        return size;
    }

    public void putHelper(BSTNode curNode, BSTNode addNode){
        int result = curNode.nodeKey.compareTo(addNode.nodeKey);
        if(result > 0 && (curNode.left == null || curNode.left.nodeKey.compareTo(addNode.nodeKey) < 0)){
            addNode.left = curNode.left;
            curNode.left = addNode;
        }
        else if (result > 0 && (curNode.left == null || curNode.left.nodeKey.compareTo(addNode.nodeKey) > 0)) {
            putHelper(curNode.left,addNode);
        } else if (result < 0 && (curNode.right == null || curNode.right.nodeKey.compareTo(addNode.nodeKey) > 0)) {
            addNode.right = curNode.right;
            curNode.right = addNode;
        }else putHelper(curNode.right, addNode);
    }
    @Override
    public void put(Object key, Object value) {
        BSTNode addNode = new BSTNode((K)key, (V)value);
        if(size == 0 || root == null) root = addNode;
        else putHelper(root, addNode);
        size += 1;
    }

    public void printInorderHelper(BSTNode curNode){
        if(curNode.left != null) printInorderHelper(curNode.left);
        else{
            System.out.print(curNode.nodeValue);
            System.out.print(" ");
            if(curNode.right != null) printInorderHelper(curNode.right);
        }
    }
    public void printInorder(){
        printInorderHelper(root);
    }

    public void keySetHelper(Set set, BSTNode curNode){
        if(curNode.left != null) keySetHelper(set, curNode.left);
        else{
            set.add(curNode.nodeKey);
            if(curNode.right != null) keySetHelper(set, curNode.right);
        }
    }
    @Override
    public Set keySet() {
        Set set = new HashSet<>();
        keySetHelper(set, root);
        return set;
    }

    public void removeHelper(){
        throw new UnsupportedOperationException("Don't support this operation.");
    }
    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Don't support this operation.");
    }

    @Override
    public Object remove(Object key, Object value) {
        throw new UnsupportedOperationException("Don't support this operation.");
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException("Don't support this operation.");
    }
}
