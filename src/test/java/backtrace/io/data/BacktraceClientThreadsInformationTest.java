package backtrace.io.data;

import backtrace.io.BacktraceClient;
import backtrace.io.BacktraceConfig;
import backtrace.io.events.RequestHandler;
import backtrace.io.http.BacktraceResult;
import net.jodah.concurrentunit.Waiter;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

public class BacktraceClientThreadsInformationTest {
    private final static String URL = "https://backtrace.io/";

    @Test
    public void gatherInformationAboutAllThreadsFromBacktraceClient() throws TimeoutException {
        // GIVEN
        final Waiter waiter = new Waiter();
        final BacktraceConfig config = new BacktraceConfig(URL);
        config.setGatherAllThreads(true);

        final BacktraceClient client = new BacktraceClient(config);
        client.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                waiter.assertTrue(data.getThreadInformationMap().size() > 1);
                waiter.resume();
                return BacktraceResult.onSuccess(data.getReport(), "");
            }
        });

        // WHEN
        client.send("test-message");

        // THEN
        waiter.await();
    }

    @Test
    public void gatherInformationAboutMainThreadFromBacktraceClient() throws TimeoutException {
        // GIVEN
        final Waiter waiter = new Waiter();
        final BacktraceConfig config = new BacktraceConfig(URL);
        config.setGatherAllThreads(false);

        final BacktraceClient client = new BacktraceClient(config);
        client.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                waiter.assertEquals(1, data.getThreadInformationMap().size());
                waiter.resume();
                return BacktraceResult.onSuccess(data.getReport(), "");
            }
        });

        // WHEN
        client.send("test-message");

        // THEN
        waiter.await();
    }
}
