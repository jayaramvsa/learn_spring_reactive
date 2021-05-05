package io.jay.learnspringreactive.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> namesList = Arrays.asList("Jill", "Junk", "Juku", "Kicku");

    @Test
    public void transformUsingMap() {
        Flux<String> stringFlux = Flux.fromIterable(namesList)
                .map(name -> name.toUpperCase())
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("JILL", "JUNK", "JUKU", "KICKU")
                .verifyComplete()
        ;
    }

    @Test
    public void transformUsingMapLength() {
        Flux<Integer> integerFlux = Flux.fromIterable(namesList)
                .map(name -> name.length())
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete()
        ;
    }

    @Test
    public void transformUsingMapLengthRepeat() {
        Flux<Integer> integerFlux = Flux.fromIterable(namesList)
                .map(name -> name.length())
                .repeat(1)
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
/*                .expectNext(4, 4, 4, 5)*/
                .verifyComplete()
        ;
    }

    @Test
    public void transformUsingMapFilter() {
        Flux<String> integerFlux = Flux.fromIterable(namesList)
                .filter(name -> name.length() > 4)
                .map(name -> name.toUpperCase())
                .log();

        StepVerifier.create(integerFlux)
                .expectNext("KICKU")
                .verifyComplete()
        ;
    }

    /**
     * <p>FlatMap is used if you've to call a DB or external element for every element in the flux
     * and it returns a Flux</p>
     */
    @Test
    public void transformUsingFlatMap() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) //A , B , C, D, E, F
                .flatMap(element -> {
                    return Flux.fromIterable(convertToList(element));
                }) //db or external service that returns a flux
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete()
        ;
    }

    @Test
    public void transformUsingFlatMapUsingParallel() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMap((element) ->
                        element.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(element -> Flux.fromIterable(element))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete()
        ;
    }

    @Test
    public void transformUsingFlatMapUsingParallelMaintainOrder() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
/*                .concatMap((element) ->
                        element.map(this::convertToList).subscribeOn(parallel()))*/
                .flatMapSequential((element) ->
                        element.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(element -> Flux.fromIterable(element))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete()
        ;
    }


    private List<String> convertToList(String element) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(element, "newValue");
    }
}
