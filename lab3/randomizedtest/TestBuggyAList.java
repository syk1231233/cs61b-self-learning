package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void simpleTest(){
        AListNoResizing<Integer> comparisonArray = new AListNoResizing<>();
        BuggyAList<Integer> testArray = new BuggyAList<>();
        comparisonArray.addLast(1);
        testArray.addLast(1);
        comparisonArray.addLast(2);
        testArray.addLast(2);
        comparisonArray.addLast(3);
        testArray.addLast(3);
        assertEquals(comparisonArray.getLast(),testArray.getLast());
        assertEquals(comparisonArray.getLast(),testArray.getLast());
        assertEquals(comparisonArray.getLast(),testArray.getLast());
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> M = new BuggyAList<>();
        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                M.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int sizeM = M.size();
                assertEquals(size,sizeM);
            } else if (operationNumber == 2 && L.size() != 0) {
                // getlast
                int p = L.getLast();
                int q = M.getLast();
                assertEquals(p,q);
                L.removeLast();
                M.removeLast();
            }
        }
    }
}
