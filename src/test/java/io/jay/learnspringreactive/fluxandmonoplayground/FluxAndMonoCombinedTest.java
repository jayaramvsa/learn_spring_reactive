package io.jay.learnspringreactive.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxAndMonoCombinedTest {

    @Test
    public void combineUsingMerge() {

        Flux<String> stringFlux1 = Flux.just("A", "B", "C");
        Flux<String> stringFlux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.merge(stringFlux1, stringFlux2).log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete()
        ;
    }

    @Test
    public void combineUsingMergeWithDelay() {

        Flux<String> stringFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> stringFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.merge(stringFlux1, stringFlux2).log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNextCount(6)
                /*.expectNext("A", "B", "C", "D", "E", "F")*/
                .verifyComplete()
        ;
    }

    @Test
    public void combineUsingConcat() {

        Flux<String> stringFlux1 = Flux.just("A", "B", "C");
        Flux<String> stringFlux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.concat(stringFlux1, stringFlux2).log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete()
        ;
    }

    @Test
    public void combineUsingConcatWithDelay() {

        Flux<String> stringFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> stringFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(stringFlux1, stringFlux2).log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete()
        ;
    }

    @Test
    public void combineUsingZip() {

        Flux<String> stringFlux1 = Flux.just("A", "B", "C");
        Flux<String> stringFlux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.zip(stringFlux1, stringFlux2, (t1, t2) -> {
            return t1.concat(t2);
        }).log(); //A,D : B,E : C:F

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete()
        ;
    }

    @Test
    public void combineUsingConcatWithDelayUsingVirtualTime() {

        VirtualTimeScheduler.getOrSet();

        Flux<String> stringFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> stringFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(stringFlux1, stringFlux2).log();

        StepVerifier.withVirtualTime(() -> mergedFlux)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete()
        ;
    }

}
