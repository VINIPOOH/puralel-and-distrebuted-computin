package task2;

import org.junit.Test;

import java.util.ArrayList;

public class SkipListTest {


    @Test
    public void testAllTogether() throws InterruptedException {
        SkipListt<String> SkipListt = new SkipListt<>(17, 0.5);
        ArrayList<Thread> threads = new ArrayList<>();
        int i = 0;
        for (; i < 10; i++) {
            threads.add(i, new Thread(() -> {
                String currThreadName = Thread.currentThread().getName();
                System.out.println("Add " + currThreadName + ": " + SkipListt.add(currThreadName));
            }));
            threads.get(i).start();
        }
        threads.add(i, new Thread(() -> {
            System.out.println("Remove " + "Thread-1" + ": " + SkipListt.remove("Thread-1"));
        }));
        threads.get(i).start();

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Contains: " + SkipListt.contains("Thread-1"));
        SkipListt.printAllList();
    }
}