package verticletest;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import verticle.Verticle;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Kyza on 28.06.2016.
 */
@RunWith(VertxUnitRunner.class)
public class FirstTest {

    private Vertx vertx;
    private Integer port;

    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port)
                );
        vertx.deployVerticle(Verticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testVerticle(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(8080, "localhost", "/",
                responce -> {
                    responce.handler(body -> {
                        context.assertTrue(body.toString().contains("Hello"));
                        async.complete();
                    });
                });
    }

    @Test
    public void chekThatIndexPageIsServed(TestContext context) {
        Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/assets/index.html",
                response -> {
                    context.assertEquals(response.statusCode(), 200);
                    context.assertEquals(response.headers().get("content-type"), "text/html");
                    response.bodyHandler(body -> {
                        context.assertTrue(body.toString().contains("<title>My PhoneBook</title>"));
                        async.complete();
                    });
                });
    }

    @Test
    public void checkAdd(TestContext context) {
        Async async = context.async();

        final String json = Json.encodePrettily(new User("Jhon", "Snow", 0b1));
        vertx.createHttpClient().post(port, "localhost", "/api/phonebook")
                .putHeader("content-type", "application-json")
                .putHeader("content-length", Integer.toString(json.length()))
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 201);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        final User user = Json.decodeValue(body.toString(), User.class);
                        context.assertEquals(user.getName(), "Jhon");
                        context.assertEquals(user.getSurname(), "Snow");
                       // context.assertEquals(user.getPhonenum(), 0b1);
                        context.assertNotNull(user.getId());
                        async.complete();
                    });
                })
                .write(json)
                .end();

    }

}
