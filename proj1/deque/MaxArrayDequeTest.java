package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;
public class MaxArrayDequeTest {
    @Test
    public void testOk(){
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addFirst(1);
        mad.addFirst(-1);
        mad.addFirst(8);
        mad.addFirst(6);
        assertEquals((int)mad.max(),8);
    }

    private static class IntComparator implements Comparator<Integer>{
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }
}
