package verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * Created by Kyza on 29.06.2016.
 */
public class VerticleJDBC  extends AbstractVerticle{

    private JDBCClient jdbcClient;

    @Override
    public void start() throws Exception {
        jdbcClient = JDBCClient.createShared(vertx, config(), "My Phonebook");
    }
}
