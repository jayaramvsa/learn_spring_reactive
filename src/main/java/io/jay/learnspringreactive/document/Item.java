package io.jay.learnspringreactive.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.BodyInserter;
import reactor.core.publisher.Mono;

@Document //Equivalent to @Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item{

    @Id
    private String id;
    private String description;
    private Double price;

}
