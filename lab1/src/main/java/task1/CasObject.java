package task1;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CasObject extends Object {

    private AtomicReference<Runnable> currentThread = new AtomicReference<>();
    private LinkedBlockingQueue<Runnable> waitingThreads;

    public CasObject(LinkedBlockingQueue<Runnable> waitingThreads) {
        this.waitingThreads = waitingThreads;
    }

    public void lock(){
        if (Thread.currentThread().equals(currentThread.get())) {
            throw new RuntimeException("can not lock mutex 2 or more times");
        }

        while (!currentThread.compareAndSet(null, Thread.currentThread())) {
            Thread.yield();
        }
        System.out.println("CasObject took by: " + Thread.currentThread().getName());
    }

    public void unlock() {
        if (!Thread.currentThread().equals(currentThread.get())) {
            throw new RuntimeException("You can't call unlock when you don't have lock");
        }

        System.out.println("CasObject unlocked by: " + Thread.currentThread().getName());
        currentThread.set(null);
    }


    public void casWait() throws InterruptedException {
        Thread thread = Thread.currentThread();
        if (!thread.equals(currentThread.get())) {
            throw new RuntimeException("You should lock mutex before use of wait method");
        }

        waitingThreads.put(thread);
        System.out.println("Waiting: " + Thread.currentThread().getName());
        unlock();

        while (waitingThreads.contains(thread)) {
            Thread.yield();
        }

        lock();
        System.out.println("No waiting any more: " + Thread.currentThread().getName());
    }

    public void casNotify() throws InterruptedException {
        waitingThreads.take();
        System.out.println("Notify: " + Thread.currentThread().getName());
    }

    public void casNotifyAll() {
        waitingThreads.clear();
        System.out.println("Notify all: " + Thread.currentThread().getName());
    }
}
