package io.jay.learnspringreactive.repository;

import io.jay.learnspringreactive.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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

    @Test
    public void findItemByDescription() {

        StepVerifier.create(itemReactiveRepository.findByDescription("MI TV").log("findItemByDescription : "))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete()
        ;
    }

    @Test
    public void saveItem() {
        Item item = new Item(null, "VU TV", 345.0);
        Mono<Item> savedItemMono = itemReactiveRepository.save(item);
        StepVerifier.create(savedItemMono.log("savedItem :"))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId() != null && item1.getDescription().equals("VU TV"))
                .verifyComplete()
        ;
    }

    @Test
    public void updateItem() {
        double updatePrice = 199.99;
        Mono<Item> itemMono = itemReactiveRepository.findByDescription("MI TV")
                .map(item -> {
                    item.setPrice(updatePrice);
                    return item;
                })
                .flatMap(item -> {
                    return itemReactiveRepository.save(item);
                });

        StepVerifier.create(itemMono.log("Updated Item :"))
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 199.99)
                .verifyComplete()
        ;
    }

    @Test
    public void deleteItemById() {
        Mono<Void> voidMono = itemReactiveRepository.findById("1")
                .map(Item::getId) //Transform from one type to another type
                .flatMap(id -> {
                    return itemReactiveRepository.deleteById(id);
                });

        StepVerifier.create(voidMono.log("Deleted Item :"))
                .expectSubscription()
                .verifyComplete()
        ;

        StepVerifier.create(itemReactiveRepository.findAll().log("Item List :"))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete()
        ;
    }

    @Test
    public void deleteItem() {
        Mono<Void> voidMono = itemReactiveRepository.findByDescription("VIDEOCON TV")
                .flatMap(item -> {
                    return itemReactiveRepository.delete(item);
                });

        StepVerifier.create(voidMono.log("Deleted Item :"))
                .expectSubscription()
                .verifyComplete()
        ;

        StepVerifier.create(itemReactiveRepository.findAll().log("Item List :"))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete()
        ;
    }
}
