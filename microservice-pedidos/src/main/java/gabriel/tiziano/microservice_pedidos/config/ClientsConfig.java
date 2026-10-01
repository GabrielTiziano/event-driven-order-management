package gabriel.tiziano.microservice_pedidos.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "gabriel.tiziano.microservice_pedidos.client")
public class ClientsConfig {
}