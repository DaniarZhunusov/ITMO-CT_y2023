package queue;

import java.util.*;

public class QueueTests {
    public static void fill() {
        for (int i = 0; i < 10; i++) {
            ArrayQueueModule.enqueue(i);
        }
    }

    public static void dump() {
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println("Size: " + ArrayQueueModule.size() +
                    " Element: " + ArrayQueueModule.element() +
                    " " + ArrayQueueModule.dequeue());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        fill();
        dump();
    }
}