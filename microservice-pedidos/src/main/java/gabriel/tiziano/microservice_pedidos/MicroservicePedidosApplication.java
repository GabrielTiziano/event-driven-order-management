package gabriel.tiziano.microservice_pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
public class MicroservicePedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicePedidosApplication.class, args);
	}

}
