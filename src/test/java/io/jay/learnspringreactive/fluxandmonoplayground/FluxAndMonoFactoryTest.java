package io.jay.learnspringreactive.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {
    List<String> namesList = Arrays.asList("Jill", "Junk", "Juku", "Kicku");

    @Test
    public void fluxUsingIterable() {

        Flux<String> namesFlux = Flux.fromIterable(namesList).log();
        StepVerifier.create(namesFlux)
                .expectNext("Jill", "Junk", "Juku", "Kicku")
                .verifyComplete()
        ;
    }

    @Test
    public void fluxUsingArray() {
        String[] strings = new String[]{"Jill", "Junk", "Juku", "Kicku"};

        Flux<String> stringFlux = Flux.fromArray(strings);

        StepVerifier.create(stringFlux)
                .expectNext("Jill", "Junk", "Juku", "Kicku")
                .verifyComplete()
        ;

    }

    @Test
    public void fluxUsingStream() {
        Flux<String> stringFlux = Flux.fromStream(namesList.stream()).log();

        StepVerifier.create(stringFlux)
                .expectNext("Jill", "Junk", "Juku", "Kicku")
                .verifyComplete()
        ;

    }

    @Test
    public void monoUsingJustOrEmpty() {
        Mono<String> stringMono = Mono.justOrEmpty(null);
        StepVerifier.create(stringMono.log())
                .verifyComplete()
        ;
    }

    @Test
    public void monoUsingSupplier() {

        Supplier<String> stringSupplier = () -> "Jill";
        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);
        System.out.println(stringSupplier.get());
        StepVerifier.create(stringMono.log())
                .expectNext("Jill")
                .verifyComplete()
        ;
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5).log();

        StepVerifier.create(integerFlux)
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }
}
