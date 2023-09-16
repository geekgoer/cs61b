package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>{
    private int size ;
    private IntNode sentinel;

    public static class IntNode<T>{
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
    public T getRecursive_helper(int index ,IntNode<T> p){
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

//    @Override
//    public boolean isEmpty() {
//        return sentinel.next == sentinel;
//    }

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
        return ret;
    }

    @Override
    public T get(int p) {
        IntNode it_node = sentinel;
        IntNode node=null;
        for(int i = 0;i <= p ;i++){
            node = it_node.next;
            if(node == null)
                return null;
        }
        return (T)node.item;
    }

    @Override
    public int size() {
        return size;
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    public boolean equals(Object o){
        return o instanceof LinkedListDeque;
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
