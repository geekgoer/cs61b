package deque;

import java.util.Iterator;

public interface Deque<T> {
    void addFirst(T x);
    void addLast(T x);
    boolean isEmpty();
    void printDeque();
    T removeFirst();
    T removeLast();
    T get(int p);
    int size();

}
