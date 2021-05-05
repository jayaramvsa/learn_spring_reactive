package io.jay.learnspringreactive.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {

        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100))
                .log();

        infiniteFlux.subscribe((element) -> System.out.println("Value is :" + element));

        Thread.sleep(10000);

    }

    @Test
    public void infiniteSequenceTest() {
        Flux<Long> longFiniteFlux = Flux.interval(Duration.ofMillis(100))
                .take(4)
                .log();

        StepVerifier.create(longFiniteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L, 3L)
                .verifyComplete()
        ;
    }

    @Test
    public void infiniteSequenceMap() {
        Flux<Integer> longFiniteFlux = Flux.interval(Duration.ofMillis(100))
                .map(element -> new Integer(element.intValue()))
                .take(4)
                .log();

        StepVerifier.create(longFiniteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2, 3)
                .verifyComplete()
        ;
    }

    /**
     * <p>
     * It essentially means that some code in the chain is too slow to process the emitted element(s) in time.
     * Take this stream for example:
     * </p>
     * <p>Flux.interval(Duration.ofSeconds(1)) //emit every second
     * .map(i -> {
     * //do something that takes 2 seconds
     * })
     * .subscribe();
     * </p>
     * <p>Every second a new value is pushed to the stream. That is Reactor tries to do that,
     * but the stream is still busy processing the previous value (which takes more than one second).
     * Please note that this example is for illustration only and it may or may not reproduce your problem.
     * </p>
     */
    @Test
    public void infiniteSequenceMapWithDelay() {
        Flux<Integer> longFiniteFlux = Flux.interval(Duration.ofMillis(1000))
                .delayElements(Duration.ofSeconds(1))
                .map(element -> new Integer(element.intValue()))
                .take(4)
                .log();

        StepVerifier.create(longFiniteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2, 3)
                .verifyComplete()
        ;
    }

}
