//package deque;
//
//import java.util.Iterator;
//
////TODO try circular arrays
//public class ArrayDeque<T> implements Deque<T> {
//    private T[] items ;
//    private int first ;
//    private int last ;
//    private int size ;
////    private int capacity = 8;
//
//    public ArrayDeque(){
//        first = 3;
//        last  = 4;
//        items = (T[]) new Object[8];
//
//    }
//    public int getFirst(){
//        return first;
//    }
//    public int getLast(){
//        return last;
//    }
//    public void resize(int capacity){
//        T[] newItems = (T[]) new Object[capacity];
//        int mid = capacity / 5 * 2;
//        int t_first = mid-1;
//        for(int i = first +1;i < last ;i++){
//            newItems[mid++] = items[i];
//        }
//        last = mid;
//        first = t_first;
//        items = newItems;
//    }
//
//    @Override
//    public void addFirst(T x) {
//        items[first--] = x;
//        size++;
//        if(first == -1){
//            resize(items.length * 2);
//        }
//    }
//
//    @Override
//    public void addLast(T x) {
//        items[last++] = x;
//        size++;
//        if(last == items.length)
//            resize(items.length * 2);
//    }
//
//    //TODO
//    @Override
//    public boolean isEmpty() {
//        return size == 0;
//    }
//
//    @Override
//    public void printDeque() {
//        for(int i = first+1;i <last;i++){
//            System.out.print(items[i]);
//            System.out.print(" ");
//        }
//        System.out.println();
//    }
//
//    @Override
//    public T removeFirst() {
//        if(isEmpty()) {
//            return null;
//        }
//        T t = items[first+1];
//        first++;
//        size--;
//        if(size <= items.length / 4)
//            resize(items.length/2);
//        return t;
//    }
//
//    @Override
//    public T removeLast() {
//        if(isEmpty())
//            return null;
//        T t = items[last-1];
//        last --;
//        size --;
//        if(size <= items.length / 4)
//            resize(items.length/2);
//        return t;
//    }
//
//    @Override
//    public T get(int p) {
//        return items[p];
//    }
//
//    @Override
//    public int size() {
//        return size;
//    }
//
//    @Override
//    public Iterator<T> iterator() {
//        return new ArrayDequeIterator();
//    }
//
//    private class ArrayDequeIterator implements Iterator<T>{
//        int idx = first+1;
//        @Override
//        public boolean hasNext() {
//            return idx+1 != last;
//        }
//
//        @Override
//        public T next() {
//            T t =  items[idx];
//            idx++;
//            return t;
//        }
//    }
//}
package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    protected T[] items;
    protected int size;
    protected int nextFirst;
    protected int nextLast;

    /**
     * Creates an empty linked list deque
     */
    public ArrayDeque() {
        size = 0;
        items = (T[]) new Object[8];
        nextFirst = 0;
        nextLast = 1;
    }

    protected int addOne(int index) {
        return (index + 1) % items.length;
    }
    protected int minusOne(int index) {
        return (index + items.length - 1) % items.length;
    }

    protected void resize(int capacity) {
        T[] resized = (T[]) new Object[capacity];

        int index = addOne(nextFirst);
        for (int i = 0; i < size; i++) {
            resized[i] = items[index];
            index = addOne(index);
        }

        nextFirst = capacity - 1;
        nextLast = size;
        items = resized;
    }

    protected void checkMul() {
        if (size == items.length) {
            resize(size * 2);
        }
    }

    protected void checkDiv() {
        int len = items.length;
        if (len >= 16 && size < len / 4) {
            resize(len / 4);
        }
    }

    /**
     * Adds an item of type T to the front of the deque.
     * You can assume that item is never null.
     */
    @Override
    public void addFirst(T item) {
        checkMul();

        items[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
        size += 1;
    }

    /**
     * Adds an item of type T to the back of the deque.
     * You can assume that item is never null.
     */
    @Override
    public void addLast(T item) {
        checkMul();

        items[nextLast] = item;
        nextLast = addOne(nextLast);
        size += 1;
    }

    /**
     * Returns the number of items in the deque.
     * @return
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    @Override
    public void printDeque() {
        int index = addOne(nextFirst);
        for (int i = 0; i < size; i++) {
            System.out.print(items[index] + " ");
            index = addOne(index);
        }
        System.out.println();
    }

    /**
     * Removes and returns the item at the front of the deque.
     * If no such item exists, returns null.
     */
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        checkDiv();

        nextFirst = addOne(nextFirst);
        T item = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        return item;
    }

    /**
     * Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.
     */
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }

        checkDiv();

        nextLast = minusOne(nextLast);
        T item = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        return item;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null.
     * Must not alter the deque!
     */
    @Override
    public T get(int index) {
//        if (size <= index || index < 0) {
//            return null;
//        }
        return items[(nextFirst + 1 + index) % items.length];
    }

    /**
     * The Deque objects we’ll make are iterable (i.e. Iterable<T>)
     * so we must provide this method to return an iterator.
     */
    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    protected class ArrayDequeIterator implements Iterator<T> {
        private int ptr;

        ArrayDequeIterator() {
            ptr = addOne(nextFirst);
        }
        public boolean hasNext() {
            return ptr != nextLast;
        }
        public T next() {
            T item =  items[ptr];
            ptr = addOne(ptr);
            return item;
        }
    }

    /**
     * Returns whether or not the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and if it contains the same contents
     * (as goverened by the generic T’s equals method) in the same order.
     * (ADDED 2/12: You’ll need to use the instance of keywords for this.)
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque other = (Deque) o;
        if (size != other.size()) {
            return false;
        }

        int index = addOne(nextFirst);
        for (int i = 0; i < size; i++) {
            if (!(items[index].equals(other.get(i)))) {
                return false;
            }
            index = addOne(index);
        }
        return true;
    }
}
