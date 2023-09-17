package deque;

import java.util.Iterator;

//TODO try circular arrays                      //important
public class ArrayDeque<T> implements Deque<T> ,Iterable<T>{
    private T[] items ;
    private int first ;
    private int last ;
    private int size ;
//    private int capacity = 8;

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
        int mid = capacity / 5 * 2;
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
        size--;
        if(size <= items.length / 4)
            resize(items.length/2);
        return t;
    }

    @Override
    public T removeLast() {
        if(isEmpty())
            return null;
        T t = items[last-1];
        last --;
        size --;
        if(size <= items.length / 4)
            resize(items.length/2);
        return t;
    }

    @Override
    public T get(int p) {
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
