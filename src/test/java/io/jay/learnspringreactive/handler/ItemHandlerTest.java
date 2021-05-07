package io.jay.learnspringreactive.handler;

import io.jay.learnspringreactive.constants.ItemConstants;
import io.jay.learnspringreactive.document.Item;
import io.jay.learnspringreactive.repository.ItemReactiveRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public List<Item> itemData() {
        return Arrays.asList(new Item("1", "Samsung TV", 400.0),
                new Item("2", "MI TV", 300.0),
                new Item("3", "SONY TV", 450.0),
                new Item("4", "VU TV", 299.0));
    }

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemData()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted Item is :" + item))
                .blockLast()
        ;
    }

    @Test
    public void getAllItems() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
        ;
    }

    @Test
    public void getAllItemsApproach2() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith(listEntityExchangeResult -> {
                    List<Item> itemList = listEntityExchangeResult.getResponseBody();
                    itemList.forEach(item -> Assert.assertTrue(item.getId() != null));
                })
        ;
    }

    @Test
    public void getAllItemsApproach3() {
        Flux<Item> itemFlux = webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete()
        ;
    }

    @Test
    public void getSingleItem() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 400.0)
        ;
    }

    @Test
    public void getSingleItemNotFound() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "10")
                .exchange()
                .expectStatus().isNotFound()
        ;
    }
    @Test
    public void createItem() {
        Item item = new Item("5", "TOSHIBA TV", 195.99);

        webTestClient.post().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("5")
                .jsonPath("$.description").isEqualTo("TOSHIBA TV")
                .jsonPath("$.price").isEqualTo(195.99)
        ;

    }

    @Test
    public void deleteItem() {
        webTestClient.delete().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class)
        ;
    }

    @Test
    public void updateItem(){
        double newPrice = 100.00;
        Item item = new Item("2", "MI TV", 300.0);

        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item),Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",newPrice);
    }

    @Test
    public void updateItemNotFound(){
        double newPrice = 100.00;
        Item item = new Item("2", "MI TV", 300.0);

        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),100)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item),Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

}
