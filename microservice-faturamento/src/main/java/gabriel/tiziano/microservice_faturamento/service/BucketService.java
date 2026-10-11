package gabriel.tiziano.microservice_faturamento.service;

import gabriel.tiziano.microservice_faturamento.config.props.StorageProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final StorageProps props;

    public void upload(String key, byte[] conteudo, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(conteudo));
        log.info("Objeto enviado ao bucket: bucket={}, key={}", props.bucket(), key);
    }

    public URL getUrl(String key, Duration expiracao) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiracao)
                .getObjectRequest(getRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url();
    }
}