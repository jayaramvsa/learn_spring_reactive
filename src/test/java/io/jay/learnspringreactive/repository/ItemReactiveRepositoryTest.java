package io.jay.learnspringreactive.repository;

import io.jay.learnspringreactive.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(new Item(null, "LG TV", 420.0),
            new Item(null, "Samsung TV", 320.0),
            new Item(null, "SONY TV", 920.0),
            new Item(null, "VIDEOCON TV", 410.0),
            new Item("1", "MI TV", 410.0));

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll().
                thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item -> {
                    System.out.println("Item is inserted" + item);
                }))
                .blockLast(); //Wait until all operations above is completed

    }

    @Test
    public void getAllItems() {

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete()
        ;
    }

    @Test
    public void getItemById() {

        StepVerifier.create(itemReactiveRepository.findById("1"))
                .expectSubscription()
                .expectNextMatches((item -> item.getDescription().equals("MI TV")))
                .verifyComplete()
        ;
    }
}
