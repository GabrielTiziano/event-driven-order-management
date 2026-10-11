package gabriel.tiziano.microservice_faturamento.config;

import gabriel.tiziano.microservice_faturamento.config.props.StorageProps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(StorageProps.class)
public class BucketConfig {

    @Bean
    public S3Client s3Client(StorageProps props) {
        return S3Client.builder()
                .endpointOverride(URI.create(props.endpoint()))
                .credentialsProvider(credentials(props))
                .region(Region.of(props.region()))
                .forcePathStyle(true)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(StorageProps props) {
        return S3Presigner.builder()
                .endpointOverride(URI.create(props.endpoint()))
                .credentialsProvider(credentials(props))
                .region(Region.of(props.region()))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    private StaticCredentialsProvider credentials(StorageProps props) {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(props.accessKey(), props.secretKey()));
    }
}