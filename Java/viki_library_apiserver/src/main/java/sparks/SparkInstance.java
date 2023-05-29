package sparks;

import io.github.manusant.ss.SparkSwagger;
import io.github.manusant.ss.conf.Options;
import io.github.manusant.ss.rest.Endpoint;
import lombok.extern.slf4j.Slf4j;
import spark.Service;

import java.io.IOException;
import java.util.Properties;

import static spark.Service.ignite;

@Slf4j
/**
 * Abstract class all sparks have to extend. Provides default configurations for each instance.
 */
public abstract class SparkInstance implements Endpoint {

    protected Service http;

    public SparkInstance() {
        this.http = ignite();
    }

    /**
     * Gets the config properties used to establish the Spark Instance
     *
     * @return
     */
    protected abstract Properties getConfigurationProperties();

    /**
     * Inits and ignites the sparks. Configure your routes here.
     */
    public abstract void initSpark() throws IOException;

    /**
     * Stops and kills this spark instance.
     */
    public void killSpark() {
        this.http.stop();
    }
}
