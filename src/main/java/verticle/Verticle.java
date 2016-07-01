package verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
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

        router.route("/assets/*").handler(StaticHandler.create("assets"));

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                startFuture.complete();
                            } else {
                                startFuture.fail(result.cause());
                            }
                        });
        router.get("/api/phonebook").handler(this::getAll);
        router.route("/api/phonebook*").handler(BodyHandler.create());
        router.post("/api/phonebook").handler(this::addone);
        router.get("/api/phonebook/:id").handler(this::getOne);
        router.put("/api/phonebook/:id").handler(this::update);
        router.delete("/api/phonebook/:id").handler(this::deleteOne);
    }
    private Map<Integer, User> userPhone = new LinkedHashMap<>();


    private void getOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            User user = userPhone.get(idAsInteger);
            if (user == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(user));
            }
        }
    }


    private void getAll(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(userPhone.values()));
    }



    private void createSomeData() {
        User firstUser = new User("Ramsi", "Bolton", 06311465433);
        userPhone.put(firstUser.getId(), firstUser);
        User secondUser = new User("Jhon", "Snow", 06311465433);
        userPhone.put(secondUser.getId(), secondUser);
        User thirdUser = new User("Daineris", "Targarien", 222);
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
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            userPhone.remove(idAsInteger);
        }
        routingContext.response().setStatusCode(204).end();
    }



    private void update(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (id == null || json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            User user = userPhone.get(idAsInteger);
            if (user == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                user.setName(json.getString("name"));
                user.setSurname(json.getString("surname"));
               // user.setPhonenum(json.getInteger("phonenum"));
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(user));
            }
        }
    }

}
