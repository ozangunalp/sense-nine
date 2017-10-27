package com.mechanitis.demo.sense.twitter;

import static com.mechanitis.demo.sense.service.ServiceFixture.connectAndWaitForSuccess;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mechanitis.demo.sense.service.MessageReceivedEndpoint;

class CannedTweetsServiceTest {
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private Path testFilePath;

    @BeforeEach
    void beforeAll() throws URISyntaxException {
        this.testFilePath = Paths.get(CannedTweetsServiceTest.class.getResource("/tweetdata60-mins.txt").toURI());
    }

    @Test
    void shouldMessageClientsWithTweetsReceived() throws Exception {
        // start service
        CannedTweetsService service = new CannedTweetsService(testFilePath);
        executor.submit(service);

        // run a client that connects to the server and finishes when it receives a message
        CountDownLatch latch = new CountDownLatch(1);
        MessageReceivedEndpoint clientEndpoint = new MessageReceivedEndpoint(latch);

        boolean success = connectAndWaitForSuccess(URI.create("ws://localhost:8081/tweets/"), clientEndpoint, latch);
        assertThat("Client endpoint should have received a message", success, is(true));

        // finally
        service.stop();
    }

    @Test
    void shouldStop() throws Exception {
        CannedTweetsService service = new CannedTweetsService(testFilePath);
        executor.submit(service);

        service.stop();
        assertThat("Should actually reach this and not wait forever", true, is(true));
    }

}