package vertx;

import io.vertx.core.json.JsonObject;
import io.vertx.core.sync.Vertx;

public interface AppSupplier {

    AppBase get(Vertx vertx, JsonObject config, boolean initDb);

}
