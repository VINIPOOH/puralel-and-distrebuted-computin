package task1;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.LinkedBlockingQueue;

@RunWith(MockitoJUnitRunner.class)
public class CasObjectTest {

    CasObject casObject;

    @Before
    public void testCasWait() throws InterruptedException {
        LinkedBlockingQueue<Runnable> listOfWaitingThreads = new LinkedBlockingQueue<>();
        casObject = new CasObject(listOfWaitingThreads);

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    casObject.lock();
                    casObject.casWait();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    casObject.unlock();
                }
            }).start();
        }

    }

    @Test
    public void testCasNotify() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            casObject.casNotify();
        }
    }

    @Test
    public void testCasNotifyAll() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(500);
            casObject.casNotifyAll();
        }
    }
}