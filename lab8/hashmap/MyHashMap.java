package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    private static final int DEFAULT_INISIZE = 16;
    private static final double DEFAULT_MAXFACTOR = 0.75;
    @Override
    public Iterator<K> iterator() {
        return new hashMapIt();
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
    private Collection<Node>[] buckets;
    private int size ;
    private double loadFactor ;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(16,0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize,0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.size = 0;
        buckets = createTable(initialSize);
        this.loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    @Override
    public void clear() {
        this.buckets = createTable(DEFAULT_INISIZE);
        this.size = 0;
        this.loadFactor = DEFAULT_MAXFACTOR;
    }

    // get idx by hashcode
    public int getNodeHashIndex(K key){
        return getNodeHashIndex(key,buckets);
    }

    // get idx in other Collection
    public int getNodeHashIndex(K key,Collection<Node>[] tarBuckets){
        return Math.floorMod(key.hashCode(),tarBuckets.length);
    }

    // find Node by idx
    public Node getNode(int idx,K key){
        for(Node node : buckets[idx]){
            if(node.key.equals(key))
                return node;
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        int idx = getNodeHashIndex(key);
        Node node = getNode(idx,key);
        return node!=null;
    }

    @Override
    public V get(K key) {
        int idx = getNodeHashIndex(key);
        Node node = getNode(idx,key);
        if(node == null)
            return null;
        return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if((double)this.size/this.buckets.length >= this.loadFactor){
            resize(this.buckets.length * 2);
        }
        int tarIdx = getNodeHashIndex(key);
        Node node = getNode(tarIdx,key);
        if(node != null){
            node.value = value;
            return;
        }
        size++;
        buckets[tarIdx].add(createNode(key,value));
    }

    public void resize(int capacity){
        Collection<Node>[] newBucket = createTable(capacity);
        hashMapIt it = new hashMapIt();
        while(it.hasNext()){
            K key = it.next();
            int tarIdx = getNodeHashIndex(key,newBucket);
            newBucket[tarIdx].add(createNode(key,get(key)));
        }
        buckets = newBucket;
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        hashMapIt it = new hashMapIt();
        while(it.hasNext()){
            set.add(it.next());
        }
        return set;
    }

    public V remove(K key){
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
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
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] colle = new Collection[tableSize];
        for(int i = 0;i < tableSize;i++){
            colle[i] = createBucket();
        }
        return colle;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    class hashMapIt implements Iterator{
        private Queue<K> q;

        public hashMapIt(){
            q = new LinkedList<>();
            for(int i = 0;i < buckets.length;i++){
                for(Node node:buckets[i]){
                    q.add(node.key);
                }
            }
        }

        @Override
        public boolean hasNext() {
            return q.size() != 0;
        }

        @Override
        public K next() {
            return q.poll();
        }
    }
}
