package deque;

import java.util.Comparator;
public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private final Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c){
        this.comparator = c;
    }

    public T max(){
        return max(comparator);
    }

    public T max(Comparator<T> c){
        if(isEmpty())
            return null;
        int maxIndex = getFirst()+1;
        for (int i = getFirst()+1; i < getLast(); i++) {
            if (c.compare(get(i), get(maxIndex)) > 0) {
                maxIndex = i;
            }
        }
        return get(maxIndex);
    }
}
