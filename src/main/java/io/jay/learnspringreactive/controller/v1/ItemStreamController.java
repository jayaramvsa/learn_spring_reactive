package io.jay.learnspringreactive.controller.v1;

import io.jay.learnspringreactive.constants.ItemConstants;
import io.jay.learnspringreactive.document.ItemCapped;
import io.jay.learnspringreactive.repository.ItemReactiveCappedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.awt.*;

@RestController
public class ItemStreamController {

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @GetMapping(value = ItemConstants.STREAM_ENDPOINT ,produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped>  getItemsSteam(){

        return itemReactiveCappedRepository.findItemsBy();
    }
}
