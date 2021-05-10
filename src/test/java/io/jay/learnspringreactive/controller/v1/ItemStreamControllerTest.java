package io.jay.learnspringreactive.controller.v1;

import io.jay.learnspringreactive.constants.ItemConstants;
import io.jay.learnspringreactive.document.ItemCapped;
import io.jay.learnspringreactive.repository.ItemReactiveCappedRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemStreamControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Before
    public void setUp() {

        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(1))
                .map(aLong -> new ItemCapped(null, "Random Item " + aLong, (100.00 + aLong)))
                .take(5);

        itemReactiveCappedRepository.insert(itemCappedFlux)
                .doOnNext(itemCapped -> {
                    System.out.println("Inserted Capped Item is :" + itemCapped);
                })
                .blockLast();
    }

    @Test
    public void testStreamAllItems() {

        Flux<ItemCapped> itemCappedFlux = webTestClient.get().uri(ItemConstants.STREAM_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);

        StepVerifier.create(itemCappedFlux.log("The Value"))
                .expectSubscription()
                .expectNextCount(5)
                .thenCancel()
                .verify();
    }

}
