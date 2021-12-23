package task4;

import org.junit.Test;

import java.util.ArrayList;

public class HarrisOrderedListTest {

    @Test
    public void test() throws InterruptedException {
        HarrisOrderedList<String> harrisOrderedList = new HarrisOrderedList<>();
        ArrayList<Thread> threads = new ArrayList<>();
        int i = 0;
        for (; i < 10; i++) {
            threads.add(i, new Thread(()->{
                String currThreadName = Thread.currentThread().getName();
                harrisOrderedList.add(currThreadName);
            }));
            threads.get(i).start();
        }
        threads.add(i, new Thread(()->{
            System.out.println("Remove: Thread-1 " + harrisOrderedList.remove("Thread-1"));
        }));
        threads.get(i).start();

        for (Thread thread: threads) {
            thread.join();
        }

        System.out.println("Contains: " + harrisOrderedList.contains("Thread-1"));
        harrisOrderedList.nonSafePrint();
    }

}