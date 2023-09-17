package deque;

import java.util.Iterator;

//public class ArrayDeque<T> implements Deque<T>,Iterable<T>{
//    private T items[];
//    private int nextFirst;
//    private int Last;
//    private int size;
//
//    public ArrayDeque(){
//        items = (T[]) new Object[8];
//        nextFirst = 0;
//        Last = 0;
//    }
//    private boolean checkFull(){
//        return nextFirst == Last;
//    }
//
//    private void resize(int capacity){
//        T[] newItems = (T[]) new Object[capacity];
//        int idx = plusOne(nextFirst);
//        for(int i = 0;i < size; i++){
//            newItems[i] = items[idx];
//            idx = plusOne(idx);
//        }
//        nextFirst = capacity-1;
//        Last = size - 1;
//        items = newItems;
//    }
//    private int minesOne(int idx){
//        return (idx+items.length-1) % items.length;
//    }
//
//    private int plusOne(int idx){
//        return (idx + 1) % items.length;
//    }
//
//    @Override
//    public void addFirst(T x) {
//        if(checkFull())
//            resize(items.length * 2);
//        size ++ ;
//        items[nextFirst] = x;
//        nextFirst = minesOne(nextFirst);
//    }
//
//    @Override
//    public void addLast(T x) {
//        if(checkFull())
//            resize(items.length * 2);
//        size ++ ;
//        Last = plusOne(Last);
//        items[Last] = x;
//    }
//
//    @Override
//    public void printDeque() {
//        int idx = plusOne(nextFirst);
//        while(minesOne(idx) != Last){
//            System.out.print(get(idx));
//            System.out.print(" ");
//            idx = plusOne(idx);
//        }
//        System.out.println();
//    }
//
//    @Override
//    public T removeFirst() {
//        if(isEmpty())
//            return null;
//        nextFirst = plusOne(nextFirst);
//        T t = get(nextFirst);
//        items[nextFirst] = null;
//        size --;
//        return t;
//    }
//
//    @Override
//    public T removeLast() {
//        return null;
//    }
//
//    @Override
//    public T get(int p) {
//        return null;
//    }
//
//    @Override
//    public int size() {
//        return 0;
//    }
//
//    @Override
//    public Iterator<T> iterator() {
//        return null;
//    }
//
//    private class ArrayDequeIterator implements Iterator<T>{
//
//        @Override
//        public boolean hasNext() {
//            return false;
//        }
//
//        @Override
//        public T next() {
//            return null;
//        }
//    }
//}


////TODO try circular arrays                      //important
public class ArrayDeque<T> implements Deque<T> ,Iterable<T>{
    private T[] items ;
    private int first ;
    private int last ;
    private int size ;

    public ArrayDeque(){
        first = 3;
        last  = 4;
        items = (T[]) new Object[8];

    }
    public boolean equals(Object o){
        if(!(o instanceof Deque)){
            return false;
        }
        Deque deque = (Deque) o;
        if(deque.size() != this.size())
            return false;
        if(o instanceof LinkedListDeque)
            for(int i = 0;i < size;i++){
                if(! this.get(i+getFirst()+1).equals(deque.get(i)))
                    return false;
            }
        else if(o instanceof ArrayDeque) {
            deque.ArrayDeque ad_deq = (deque.ArrayDeque) deque;
            for(int i = getFirst()+1 ;i < getLast(); i++)
                if(! get(i).equals(deque.get(i)))
                    return false;
        }
        return true;
    }
    public int getFirst(){
        return first;
    }
    public int getLast(){
        return last;
    }
    public void resize(int capacity){
        T[] newItems = (T[]) new Object[capacity];
        int mid = capacity * 2/ 5 ;
        int t_first = mid-1;
        for(int i = first +1;i < last ;i++){
            newItems[mid++] = items[i];
        }
        last = mid;
        first = t_first;
        items = newItems;
    }

    @Override
    public void addFirst(T x) {
        items[first--] = x;
        size++;
        if(first == -1){
            resize(items.length * 2);
        }
    }

    @Override
    public void addLast(T x) {
        items[last++] = x;
        size++;
        if(last == items.length)
            resize(items.length * 2);
    }

    //TODO
//    @Override
//    public boolean isEmpty() {
//        return size == 0;
//    }

    @Override
    public void printDeque() {
        for(int i = first+1;i <last;i++){
            System.out.print(items[i]);
            System.out.print(" ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if(isEmpty()) {
            return null;
        }
        T t = items[first+1];
        first++;
        items[first] = null;
        size--;
        if(size <= items.length / 4 && items.length >= 16)
            resize(items.length/2);
        return t;
    }

    @Override
    public T removeLast() {
        if(isEmpty())
            return null;
        T t = items[last-1];
        last --;
        items[last] = null;
        size --;
        if(size <= items.length / 4 && items.length >= 16)
            resize(items.length/2);
        return t;
    }

    @Override
    public T get(int p) {
        if(p < 0 || p >= size)
            return null;
        return items[p];
    }

    @Override
    public int size() {
        return size;
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T>{
        int idx ;
        public ArrayDequeIterator(){
            idx = first + 1;
        }
        @Override
        public boolean hasNext() {
            return idx != last;
        }

        @Override
        public T next() {
            T t =  items[idx];
            idx++;
            return t;
        }
    }
}
