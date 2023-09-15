package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> aln = new AListNoResizing<>();
        BuggyAList<Integer> ba = new BuggyAList<>();
        aln.addLast(3);
        ba.addLast(3);
        aln.addLast(4);
        ba.addLast(4);
        assertEquals(aln.removeLast(),ba.removeLast());
        assertEquals(aln.removeLast(),ba.removeLast());
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();
        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int bsize = B.size();
                assertEquals(size,bsize);
            } else if (operationNumber == 2) {
                //getLast
                if(L.size()>0) {
                    int ret = L.getLast();
                    int bret = B.getLast();
                    assertEquals(ret,bret);
                }
            }else{
                if(L.size()>0){
                    int ret = L.removeLast();
                    int bret = B.removeLast();
                    assertEquals(ret,bret);
//                    System.out.println("rem: "+ret);
                }
            }
        }
    }
}
