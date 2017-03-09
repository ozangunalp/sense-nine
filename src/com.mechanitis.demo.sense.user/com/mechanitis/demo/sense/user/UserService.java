package com.mechanitis.demo.sense.user;

import com.mechanitis.demo.sense.service.Service;
import io.reactivex.Flowable;

import static com.mechanitis.demo.sense.flow.PublisherFromFlowAdaptor.*;
import static com.mechanitis.demo.sense.flow.SubscriberFromFlowAdaptor.*;

public class UserService implements Runnable {
    private static final int PORT = 8083;
    private final Service service;

    private UserService() {
        service = new Service("ws://localhost:8081/tweets/", "/users/", PORT,
                (publisher, subscriber) -> Flowable.fromPublisher(toPublisher(publisher))
                        .map(UserService::getTwitterHandleFromTweet)
                        .subscribe(toSubscriber(subscriber)));
    }

    @Override
    public void run() {
        service.run();
    }

    private static String getTwitterHandleFromTweet(String fullTweet) {
        int fieldStartIndex = fullTweet.indexOf("\"screen_name\":\"")+ "\"screen_name\":\"".length();
        int fieldEndIndex = fullTweet.indexOf("\"", fieldStartIndex);
        return fullTweet.substring(fieldStartIndex, fieldEndIndex);
    }

    public static void main(String[] args) {
        new UserService().run();
    }
}
