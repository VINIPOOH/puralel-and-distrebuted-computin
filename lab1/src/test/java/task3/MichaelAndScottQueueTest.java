package task3;

import org.junit.Test;

import java.util.ArrayList;

public class MichaelAndScottQueueTest {

    @Test
    public void test() throws InterruptedException {
        MichaelAndScottQueue<String> michaelAndScottQueue = new MichaelAndScottQueue<>();

        ArrayList<Thread> threads = new ArrayList<>();
        int i = 0;
        for (; i < 100; i++) {
            threads.add(i, new Thread(() -> {
                michaelAndScottQueue.add(Thread.currentThread().getName());
                System.out.println("Add: " + michaelAndScottQueue.pull());
                if (Math.random() > 0.5) {
                    System.out.println("Remove: " + michaelAndScottQueue.pull());
                }
            }));
            threads.get(i).start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        michaelAndScottQueue.print();
    }

}