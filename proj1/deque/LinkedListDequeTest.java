package deque;

import org.junit.Test;
import static org.junit.Assert.*;



/**
 * 双端队列测试类，覆盖 ArrayDeque 和 LinkedListDeque 的所有核心功能
 * 符合 UC Berkeley CS61B proj1 测试要求
 */
public class LinkedListDequeTest {

    // ====================== ArrayDeque 核心测试 ======================
    /**
     * 测试头插、头删功能
     */
    @Test
    public void testArrayDequeAddFirstAndRemoveFirst() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        // 断言删除顺序正确
        assertEquals((Integer) 3, deque.removeFirst());
        assertEquals((Integer) 2, deque.removeFirst());
        assertEquals((Integer) 1, deque.removeFirst());
        // 断言队列空
        assertTrue(deque.isEmpty());
    }

    /**
     * 测试尾插、尾删功能
     */
    @Test
    public void testArrayDequeAddLastAndRemoveLast() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);
        // 断言删除顺序正确
        assertEquals((Integer) 3, deque.removeLast());
        assertEquals((Integer) 2, deque.removeLast());
        assertEquals((Integer) 1, deque.removeLast());
        // 断言队列空
        assertTrue(deque.isEmpty());
    }

    /**
     * 测试空队列的边界操作
     */
    @Test
    public void testArrayDequeEmptyOperations() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        // 空队列删除返回null
        assertNull(deque.removeFirst());
        assertNull(deque.removeLast());
        // 空队列get返回null
        assertNull(deque.get(0));
        // 断言size为0
        assertEquals(0, deque.size());
        // 断言队列空
        assertTrue(deque.isEmpty());
    }

    /**
     * 测试单元素队列的操作
     */
    @Test
    public void testArrayDequeSingleElement() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.addFirst(5);
        assertEquals((Integer) 5, deque.removeFirst());
        deque.addLast(6);
        assertEquals((Integer) 6, deque.removeLast());
        assertTrue(deque.isEmpty());
    }

    /**
     * 测试动态扩容和缩容
     */
    @Test
    public void testArrayDequeResize() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        // 初始容量8，添加9个元素触发扩容
        for (int i = 0; i < 9; i++) {
            deque.addLast(i);
        }
        // 断言扩容后size正确
        assertEquals(9, deque.size());
        // 断言扩容后元素顺序正确
        for (int i = 0; i < 9; i++) {
            assertEquals((Integer) i, deque.get(i));
        }
        // 删除到2个元素，触发缩容
        for (int i = 0; i < 7; i++) {
            deque.removeFirst();
        }
        // 断言缩容后size正确
        assertEquals(2, deque.size());
        // 断言缩容后元素正确
        assertEquals((Integer) 7, deque.get(0));
        assertEquals((Integer) 8, deque.get(1));
    }

    /**
     * 测试get方法
     */
    @Test
    public void testArrayDequeGet() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.addLast(10);
        deque.addLast(20);
        deque.addLast(30);
        // 断言正常索引
        assertEquals((Integer) 10, deque.get(0));
        assertEquals((Integer) 20, deque.get(1));
        assertEquals((Integer) 30, deque.get(2));
        // 断言越界返回null
        assertNull(deque.get(3));
    }

    /**
     * 测试迭代器功能
     */
    @Test
    public void testArrayDequeIterator() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);
        int count = 0;
        // 增强for循环遍历
        for (Object num : deque) {
            assertEquals((Integer) (count + 1), (Integer) num);
            count++;
        }
        // 断言遍历次数正确
        assertEquals(3, count);
    }

    // ====================== LinkedListDeque 核心测试 ======================
    /**
     * 测试头插、头删功能
     */
    @Test
    public void testLinkedListDequeAddFirstAndRemoveFirst() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        // 断言删除顺序正确
        assertEquals((Integer) 3, deque.removeFirst());
        assertEquals((Integer) 2, deque.removeFirst());
        assertEquals((Integer) 1, deque.removeFirst());
        // 断言队列空
        assertTrue(deque.isEmpty());
    }

    /**
     * 测试尾插、尾删功能
     */
    @Test
    public void testLinkedListDequeAddLastAndRemoveLast() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);
        // 断言删除顺序正确
        assertEquals((Integer) 3, deque.removeLast());
        assertEquals((Integer) 2, deque.removeLast());
        assertEquals((Integer) 1, deque.removeLast());
        // 断言队列空
        assertTrue(deque.isEmpty());
    }

    /**
     * 测试空队列的边界操作
     */
    @Test
    public void testLinkedListDequeEmptyOperations() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        // 空队列删除返回null
        assertNull(deque.removeFirst());
        assertNull(deque.removeLast());
        // 空队列get返回null
        assertNull(deque.get(0));
        // 断言size为0
        assertEquals(0, deque.size());
        // 断言队列空
        assertTrue(deque.isEmpty());
    }

    /**
     * 测试单元素队列的操作
     */
    @Test
    public void testLinkedListDequeSingleElement() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addFirst(5);
        assertEquals((Integer) 5, deque.removeFirst());
        deque.addLast(6);
        assertEquals((Integer) 6, deque.removeLast());
        assertTrue(deque.isEmpty());
    }

    /**
     * 测试get方法
     */
    @Test
    public void testLinkedListDequeGet() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addLast(10);
        deque.addLast(20);
        deque.addLast(30);
        // 断言正常索引
        assertEquals((Integer) 10, deque.get(0));
        assertEquals((Integer) 20, deque.get(1));
        assertEquals((Integer) 30, deque.get(2));
        // 断言越界返回null
        assertNull(deque.get(3));
    }

    /**
     * 测试迭代器功能
     */
    @Test
    public void testLinkedListDequeIterator() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);
        int count = 0;
        // 增强for循环遍历
        for (int num : deque) {
            assertEquals((Integer) (count + 1), (Integer) num);
            count++;
        }
        // 断言遍历次数正确
        assertEquals(3, count);
    }

    // ====================== 对比测试（两种队列功能一致性） ======================
    /**
     * 测试ArrayDeque和LinkedListDeque功能完全一致
     */
    @Test
    public void testDequeFunctionComparison() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        LinkedListDeque<Integer> linkedDeque = new LinkedListDeque<>();

        // 执行相同操作
        arrayDeque.addFirst(1);
        linkedDeque.addFirst(1);
        arrayDeque.addLast(2);
        linkedDeque.addLast(2);
        arrayDeque.addFirst(3);
        linkedDeque.addFirst(3);

        // 断言删除结果一致
        assertEquals(arrayDeque.removeFirst(), linkedDeque.removeFirst());
        assertEquals(arrayDeque.removeLast(), linkedDeque.removeLast());
        assertEquals(arrayDeque.removeFirst(), linkedDeque.removeFirst());

        // 断言最终状态一致
        assertTrue(arrayDeque.isEmpty());
        assertTrue(linkedDeque.isEmpty());
    }
}