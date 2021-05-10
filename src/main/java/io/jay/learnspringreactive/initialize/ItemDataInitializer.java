package io.jay.learnspringreactive.initialize;

import io.jay.learnspringreactive.document.Item;
import io.jay.learnspringreactive.document.ItemCapped;
import io.jay.learnspringreactive.repository.ItemReactiveCappedRepository;
import io.jay.learnspringreactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;


    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
        createCappedCollections();
        dataSetupForCappedCollection();
    }

    private void createCappedCollections() {

        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());

    }

    public List<Item> itemData() {
        return Arrays.asList(new Item("1", "Samsung TV", 400.0),
                new Item("2", "MI TV", 300.0),
                new Item("3", "SONY TV", 450.0),
                new Item("4", "VU TV", 299.0));
    }

    public void dataSetupForCappedCollection() {

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(aLong -> new ItemCapped(null, "Random Item " + aLong, (100.00 + aLong)));
        itemReactiveCappedRepository.insert(itemCappedFlux)
        .subscribe(itemCapped -> {
            log.info("Inserted Capped Item is :" + itemCapped);
        });
    }

    private void initialDataSetup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemData()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> {
                    System.out.println("Item inserted : " + item);
                })
        ;
    }

}
