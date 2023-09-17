package deque;

import java.util.ArrayDeque;
import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>,Iterable<T>{
    private int size ;
    private IntNode sentinel;

    private static class IntNode<T>{
        public IntNode prev;
        public T item;
        public IntNode next;

        public IntNode(T x,IntNode prev,IntNode next){
            this.item = x;
            this.prev = prev;
            this.next = next;
        }
    }

    public LinkedListDeque(){
        sentinel = new IntNode<T>(null,null,null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    /*
    Help for Recursive.
     **/
    private T getRecursive_helper(int index ,IntNode<T> p){
        if(p == sentinel)
            return null;
        if(index == 0)
            return p.item;
        return (T) getRecursive_helper(index-1, p.next);
    }

    public T getRecursive(int index){
        IntNode<T> p = sentinel.next;
        if(index < 0 || index >=size)
            return null;
        return getRecursive_helper(index,p);
    }

    @Override
    public void addFirst(T x) {
        size++;
        IntNode<T> p = new IntNode<>(x,sentinel,sentinel.next);
        sentinel.next.prev = p;
        sentinel.next = p;
    }

    @Override
    public void addLast(T x) {
        size++;
        IntNode<T> p = new IntNode<>(x,sentinel.prev,sentinel);
        sentinel.prev.next = p;
        sentinel.prev = p;
    }


    @Override
    public void printDeque() {
        IntNode<T> p = sentinel.next;
        for(int i = 0;i < size;i++){
            System.out.print(p.item);
            System.out.print(" ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if(size == 0)
            return null;
        size--;
        IntNode<T> t = sentinel.next;
        T res = t.item;
        sentinel.next = t.next;
        t.next.prev = sentinel;
        return res;
    }

    @Override
    public T removeLast() {
        if(size == 0)
            return null;
        size--;
        T ret =  (T)sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        return ret;
    }

    @Override
    public T get(int p) {
        IntNode it_node = sentinel.next;
        while(it_node != sentinel && p > 0){
            it_node = it_node.next;
            p --;
        }
        return (p == 0) ? (T) it_node.item:null;
    }

    @Override
    public int size() {
        return size;
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
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
                if(! this.get(i).equals(deque.get(i)))
                    return false;
            }
        else if(o instanceof ArrayDeque) {
            deque.ArrayDeque ad_deq = (deque.ArrayDeque) deque;
            for (int i = 0; i < size; i++) {
                if (!this.get(i).equals(ad_deq.get(i + ad_deq.getFirst()+1)));
            }
        }
        return true;
    }

    private class LinkedListDequeIterator implements Iterator<T>{
        private IntNode<T> p ;

        public LinkedListDequeIterator(){
            p = sentinel.next;
        }
//        @Override
        public boolean hasNext() {
//            return p.next != sentinel;
            return p != sentinel;
        }

//        @Override
        public T next() {
//            if(!hasNext())
//                return null;
            T res = p.item;
            p = p.next;
            return res;
        }
    }
}
