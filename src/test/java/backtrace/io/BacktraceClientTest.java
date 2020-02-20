package backtrace.io;

import backtrace.io.data.BacktraceData;
import backtrace.io.events.RequestHandler;
import backtrace.io.http.BacktraceResult;
import net.jodah.concurrentunit.Waiter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class BacktraceClientTest {
    private final String THREAD_NAME = "backtrace-daemon";

    @Test(expected = NullPointerException.class)
    public void initBacktraceClientWithNullConfig() {
        // WHEN
        new BacktraceClient(null);
    }

    @Test
    public void awaitingTime() throws InterruptedException {
        // GIVEN
        BacktraceConfig config = new BacktraceConfig("url", "token");
        BacktraceClient backtraceClient = new BacktraceClient(config);
        backtraceClient.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                try {
                    System.out.println("Waiting on request");
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return BacktraceResult.onSuccess(data.getReport(), "");
            }
        });

        // WHEN
        backtraceClient.send("");

        // THEN
        try{
            boolean result = backtraceClient.await(2, TimeUnit.SECONDS);
            Assert.assertFalse(result);
        }
        catch (Exception e){
            Assert.fail(e.toString());
        }
        backtraceClient.close();
    }

    @Test
    public void closeBacktraceClient() throws InterruptedException {
        // GIVEN
        BacktraceConfig backtraceConfig = new BacktraceConfig("https://backtrace.io/");
        BacktraceClient backtraceClient = new BacktraceClient(backtraceConfig);

        // WHEN
        System.out.println("working threads..");
        boolean isBacktraceThreadRunning = isBacktraceThreadRunning();
        backtraceClient.close();

        System.out.println("working threads..");
        //Let's wait to see server thread stopped
        TimeUnit.MILLISECONDS.sleep(2000);

        boolean isBacktraceThreadRunningAfterClose = isBacktraceThreadRunning();

        System.out.println(isBacktraceThreadRunning);
        // THEN
        Assert.assertTrue(isBacktraceThreadRunning);
        Assert.assertFalse(isBacktraceThreadRunningAfterClose);
    }

//    @Test
//    public void closeBacktraceClientWithSendingReport() throws InterruptedException, TimeoutException {
//        // GIVEN
//        BacktraceConfig backtraceConfig = new BacktraceConfig("https://backtrace.io/");
//        Waiter waiter = new Waiter();
//        backtraceConfig.setRequestHandler(new RequestHandler() {
//            @Override
//            public BacktraceResult onRequest(BacktraceData data) {
//                try {
//                    Thread.sleep(1000);
//                    waiter.resume();
//                } catch (InterruptedException e) {
//                    waiter.fail(e);
//                }
//                return BacktraceResult.onSuccess(data.getReport(), data.getReport().getMessage());
//            }
//        });
//        BacktraceClient backtraceClient = new BacktraceClient(backtraceConfig);
//
//        // WHEN
//        boolean isBacktraceThreadRunning = isBacktraceThreadRunning();
//        backtraceClient.send("test-message");
//        backtraceClient.close();
//        TimeUnit.MILLISECONDS.sleep(200);
//        boolean isBacktraceThreadRunningAfterClose = isBacktraceThreadRunning();
//        waiter.await();
//
//        // THEN
//        Assert.assertTrue(isBacktraceThreadRunning);
//        Assert.assertFalse(isBacktraceThreadRunningAfterClose);
//    }

    private boolean isBacktraceThreadRunning(){
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parentGroup;
        while ((parentGroup = rootGroup.getParent()) != null) {
            rootGroup = parentGroup;
        }

        Thread[] threads = new Thread[rootGroup.activeCount()];
        while (rootGroup.enumerate(threads, true ) == threads.length) {
            threads = new Thread[threads.length * 2];
        }
//        while (rootGroup.enumerate(threads, true ) == threads.length) {
//            threads = new Thread[threads.length * 2];
//        }
//
//        Set<Thread> threads = Thread.getAllStackTraces().keySet();

        for (Thread t : threads) {
            if (t == null){
                continue;
            }
            System.out.println(t.getName());
            if (t.getName().equals(THREAD_NAME)) {
                System.out.println(t.getState().name());
                return true;
            }
        }
        return false;
    }

}