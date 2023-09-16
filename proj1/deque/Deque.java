package deque;

import java.util.Iterator;

public interface Deque<T> {
    void addFirst(T x);
    void addLast(T x);
    default boolean isEmpty(){
        return size() == 0;
    }
    void printDeque();
    T removeFirst();
    T removeLast();
    T get(int p);
    int size();

}
