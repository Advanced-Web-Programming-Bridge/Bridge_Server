package gachon.bridge.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class BridgeConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(BridgeConfigApplication.class, args);
    }

}
