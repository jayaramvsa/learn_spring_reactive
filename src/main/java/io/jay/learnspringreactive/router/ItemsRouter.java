package io.jay.learnspringreactive.router;

import io.jay.learnspringreactive.constants.ItemConstants;
import io.jay.learnspringreactive.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.jay.learnspringreactive.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemHandler itemHandler) {

        return RouterFunctions.route(GET(ITEM_FUNCTIONAL_END_POINT_V1).
                and(accept(MediaType.APPLICATION_JSON)), itemHandler::getAllItems)
                .andRoute(GET(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}")).
                        and(accept(MediaType.APPLICATION_JSON)), itemHandler::getOneItem)
                .andRoute(POST(ITEM_FUNCTIONAL_END_POINT_V1)
                        .and(accept(MediaType.APPLICATION_JSON)), itemHandler::createItem)
                .andRoute(DELETE(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"))
                        .and(accept(MediaType.APPLICATION_JSON)), itemHandler::deleteItem)
                .andRoute(PUT(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"))
                        .and(accept(MediaType.APPLICATION_JSON)), itemHandler::updateItem);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemHandler itemHandler){

        return RouterFunctions.route(GET("/fun/runtimeException").
                and(accept(MediaType.APPLICATION_JSON)), itemHandler::itemException);
    }

    @Bean
    public  RouterFunction<ServerResponse> itemStreamRoute(ItemHandler itemHandler){
        return  RouterFunctions.route(GET(ItemConstants.ITEM_STREAM_FUNCTIONAL_END_POINT_V1)
                .and(accept(MediaType.APPLICATION_JSON)),itemHandler::itemsStream);
    }
}
