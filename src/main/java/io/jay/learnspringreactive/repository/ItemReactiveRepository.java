package io.jay.learnspringreactive.repository;

import io.jay.learnspringreactive.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
}
