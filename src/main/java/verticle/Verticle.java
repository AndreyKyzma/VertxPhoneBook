package verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import model.User;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Kyza on 26.06.2016.
 */
public class Verticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        createSomeData();

        Router router = Router.router(vertx);

        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1> Hello from my first Vert.x 3 app</h1>");
        });

        router.route("/assets/*").handler(StaticHandler.create("assets"));

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        config().getInteger("http.port", 8080),
                        result ->{
                        if(result.succeeded()){
                            startFuture.complete();
                        }
                         else{
                            startFuture.fail(result.cause());
                        }
                        });
        router.get("/api/phonebook").handler(this::getAll);
        router.route("/api/phonebook").handler(BodyHandler.create());
        router.post("/api/phonebook").handler(this::addone);
        router.delete("/api/phonebook/:id").handler(this::deleteOne);
    }



    private void getAll(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(userPhone.values()));
    }


        private Map<Integer, User> userPhone = new LinkedHashMap<>();

    private void createSomeData(){
        User firstUser = new User("Ramsi", "Bolton", 06311465433);
        userPhone.put(firstUser.getId(), firstUser);
        User secondUser = new User("Jhon", "Snow", 06311465433);
        userPhone.put(secondUser.getId(), secondUser);
        User thirdUser = new User("Daineris", "Targarien",222);
        userPhone.put(thirdUser.getId(), thirdUser);
    }

    private void addone(RoutingContext routingContext) {
        final User user = Json.decodeValue(routingContext.getBodyAsString(),
                User.class);
        userPhone.put(user.getId(), user);
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(user));
    }

    private void deleteOne(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
        if(id == null){
            routingContext.response().setStatusCode(400).end();
        }
        else {
            Integer idAsInteger = Integer.valueOf(id);
            userPhone.remove(idAsInteger);
        }
        routingContext.response().setStatusCode(204).end();
    }

}
