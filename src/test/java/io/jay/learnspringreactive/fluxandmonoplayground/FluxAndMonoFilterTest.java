package io.jay.learnspringreactive.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {
    List<String> namesList = Arrays.asList("Jill", "Junk", "Kuku", "Kicku");

    @Test
    public void filterTest() {
        Flux<String> stringFlux = Flux.fromIterable(namesList)
                .filter(name -> name.startsWith("J"))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Jill", "Junk")
                .verifyComplete();

    }

    @Test
    public void filterTestLength() {
        Flux<String> stringFlux = Flux.fromIterable(namesList)
                .filter(name -> name.length() > 4)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Kicku")
                .verifyComplete();

    }
}
