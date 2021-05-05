package io.jay.learnspringreactive.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    @Test
    public void coldPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        stringFlux.subscribe(element -> System.out.println("Subscriber 1:" + element));
        Thread.sleep(2000);
        stringFlux.subscribe(element -> System.out.println("Subscriber 2:" + element));
        Thread.sleep(4000);
    }

    @Test
    public void hotPublisherTest() throws InterruptedException {

        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1)).log();

        ConnectableFlux<String> stringConnectableFlux  =  stringFlux.publish();
        stringConnectableFlux.connect();
        stringConnectableFlux.subscribe(element -> System.out.println("Subscriber 1:" + element));
        Thread.sleep(3000);
        stringConnectableFlux.subscribe(element -> System.out.println("Subscriber 2:" + element));
        Thread.sleep(4000);
    }
}
