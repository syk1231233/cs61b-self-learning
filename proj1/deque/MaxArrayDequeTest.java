package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;

/**
 * MaxArrayDeque 测试类
 * 覆盖：空队列、整数比较、字符串长度比较、临时Comparator
 * 完全符合 UC Berkeley CS61B proj1 要求
 */
public class MaxArrayDequeTest {

    // ==================== 测试1：空队列调用 max() 返回 null ====================
    @Test
    public void testEmptyDequeMax() {
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(Integer::compareTo);
        assertNull(deque.max()); // 空队列，max 必须返回 null
    }

    // ==================== 测试2：整数自然排序（升序）找最大值 ====================
    @Test
    public void testIntegerNaturalMax() {
        // 传入整数默认比较器：(a, b) -> a - b
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(Integer::compareTo);

        deque.addLast(5);
        deque.addLast(2);
        deque.addLast(9);
        deque.addLast(1);
        deque.addLast(7);

        // 预期最大值：9
        assertEquals((Integer) 9, deque.max());
    }

    // ==================== 测试3：单元素队列测试 ====================
    @Test
    public void testSingleElement() {
        MaxArrayDeque<String> deque = new MaxArrayDeque<>(Comparator.comparing(String::length));
        deque.addLast("CS61B");

        // 唯一元素就是最大值
        assertEquals("CS61B", deque.max());
    }

    // ==================== 测试4：字符串按【长度】比较找最大值 ====================
    @Test
    public void testStringLengthMax() {
        // 比较规则：按字符串长度升序
        Comparator<String> lengthCmp = Comparator.comparing(String::length);
        MaxArrayDeque<String> deque = new MaxArrayDeque<>(lengthCmp);

        deque.addLast("a");
        deque.addLast("bb");
        deque.addLast("ccc");
        deque.addLast("dddd");

        // 最长字符串：dddd
        assertEquals("dddd", deque.max());
    }

    // ==================== 测试5：使用【临时 Comparator】找最大值 ====================
    @Test
    public void testMaxWithCustomComparator() {
        // 默认比较器：整数升序
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>((a, b) -> a - b);
        deque.addLast(3);
        deque.addLast(8);
        deque.addLast(1);
        deque.addLast(10);

        // 临时比较器：整数【降序】→ 此时找的是【最小值】
        Comparator<Integer> reverseCmp = (a, b) -> b - a;
        assertEquals((Integer) 1, deque.max(reverseCmp));

        // 默认比较器依然正常：返回最大值10
        assertEquals((Integer) 10, deque.max());
    }
}
