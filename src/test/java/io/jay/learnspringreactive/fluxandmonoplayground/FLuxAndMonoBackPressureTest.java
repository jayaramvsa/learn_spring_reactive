package io.jay.learnspringreactive.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

public class FLuxAndMonoBackPressureTest {

    @Test
    public void backPressureTest() {

        Flux<Integer> integerFlux = Flux.range(1, 10).log();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenRequest(1)
                .expectNext(3)
                .thenCancel()
                .verify()
        ;
    }

    @Test
    public void backPressure() {

        Flux<Integer> integerFlux = Flux.range(1, 10).log();

        integerFlux.subscribe((element) -> System.out.println("Element is : " + element)
                , (element) -> System.err.println("Exception is :" + element)
                , () -> System.out.println("Done")
                , (subscription -> subscription.request(2)));

    }

    @Test
    public void backPressureCancel() {

        Flux<Integer> integerFlux = Flux.range(1, 10).log();

        integerFlux.subscribe((element) -> System.out.println("Element is : " + element)
                , (element) -> System.err.println("Exception is :" + element)
                , () -> System.out.println("Done")
                , (subscription -> subscription.cancel()));

    }

    @Test
    public void backPressureCustomized() {

        Flux<Integer> integerFlux = Flux.range(1, 10).log();

        integerFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value is :" + value);
                if (value == 4) cancel();
            }
        });

    }
}
