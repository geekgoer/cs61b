//package deque;
//
//import java.util.Iterator;
//
//public interface Deque<T> {
//    void addFirst(T x);
//    void addLast(T x);
//    boolean isEmpty();
//    void printDeque();
//    T removeFirst();
//    T removeLast();
//    T get(int p);
//    int size();
//    public Iterator<T> iterator();
//    public boolean equals(Object o);
//}
package deque;

public interface Deque<T> {
    void addFirst(T item);
    void addLast(T item);
    default boolean isEmpty() {
        return size() == 0;
    };
    int size();
    void printDeque();
    T removeFirst();
    T removeLast();
    T get(int index);
}