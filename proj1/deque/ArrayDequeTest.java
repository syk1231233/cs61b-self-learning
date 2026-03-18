package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void simpleTest(){
        LinkedListDeque<Integer> comparisonArray = new LinkedListDeque<>();
        ArrayDeque<Integer> testArray = new ArrayDeque<>();
        comparisonArray.addLast(1);
        testArray.addLast(1);
        comparisonArray.addLast(2);
        testArray.addLast(2);
        comparisonArray.addLast(3);
        testArray.addLast(3);
        assertEquals(comparisonArray.removeLast(),testArray.removeLast());
        assertEquals(comparisonArray.removeLast(),testArray.removeLast());
        assertEquals(comparisonArray.removeLast(),testArray.removeLast());
    }

    @Test
    public void addtest(){
        ArrayDeque<Integer> l = new ArrayDeque<>();
        l.addFirst(4);
        l.addLast(5);
        l.addFirst(3);
        l.addLast(6);
        l.addFirst(2);
        l.addLast(7);
        l.addLast(8);
        l.addFirst(1);
        assertEquals(1,l.removeFirst());
        assertEquals(8,l.removeLast());
        assertEquals(7,l.removeLast());
        assertEquals(2,l.removeFirst());
        assertEquals(6,l.removeLast());
        assertEquals(5,l.removeLast());
        assertEquals(3,l.removeFirst());
        assertEquals(4,l.removeFirst());
    }

    @Test
    public void randomizedTest(){
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        ArrayDeque<Integer> M = new ArrayDeque<>();
        int N = 50000; // 随机操作次数

        for (int i = 0; i < N; i++) {
            // 随机数：0=addLast,1=addFirst,2=removeLast,3=removeFirst,4=size,5=get
            int operationNumber = StdRandom.uniform(0, 6);

            if (operationNumber == 0) {
                // 尾插
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                M.addLast(randVal);
            }
            else if (operationNumber == 1) {
                // 头插
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                M.addFirst(randVal);
            }
            else if (operationNumber == 2 && L.size() > 0) {
                // 尾删（核心修复：删掉多余的removeLast！）
                int p = L.removeLast();
                int q =(int) M.removeLast();
                assertEquals(p, q); // 断言删除的值一致
            }
            else if (operationNumber == 3 && L.size() > 0) {
                // 头删
                int p = L.removeFirst();
                int q =(int) M.removeFirst();
                assertEquals(p, q);
            }
            else if (operationNumber == 4) {
                // 校验size完全一致
                assertEquals(L.size(), M.size());
            }
            else if (operationNumber == 5 && L.size() > 0) {
                // 随机索引get校验
                int randomIndex = StdRandom.uniform(0, L.size());
                assertEquals(L.get(randomIndex), M.get(randomIndex));
            }
        }
    }
}
