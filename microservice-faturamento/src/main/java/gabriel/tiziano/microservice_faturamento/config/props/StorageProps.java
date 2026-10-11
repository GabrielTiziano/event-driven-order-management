package gabriel.tiziano.microservice_faturamento.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "order-management-microservices.storage")
public record StorageProps(
        String endpoint,
        String accessKey,
        String secretKey,
        String region,
        String bucket
) {
}