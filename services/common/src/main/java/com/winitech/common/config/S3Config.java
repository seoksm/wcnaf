package com.winitech.common.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.http.conn.ssl.SdkTLSSocketFactory;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * S3 설정을 위한 클래스
 * <pre>
 * com.winitech.common.config
 * └ S3Config.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 24-12-13 09:03:00
 **/
@Slf4j
@Configuration
public class S3Config {
	@Value("${cloud.aws.credentials.accessKey:}")
	private String accessKey;
	@Value("${cloud.aws.credentials.secretKey:}")
	private String secretKey;
	@Value("${cloud.aws.region.static:}")
	private String region;
	@Value("${cloud.aws.s3.customEndpoint:}")
	private String customEndpoint;

	@Bean
	public AmazonS3 amazonS3Client() {
		if (accessKey.isEmpty() || secretKey.isEmpty()) {
			log.info("AWS S3 설정이 없습니다.");
//			return null;
		}
		
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		if (customEndpoint == null || customEndpoint.isEmpty()) {
			// 커스텀 엔드포인트가 없는 경우 AWS S3 서비스를 사용
			
			return AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials))
					.withRegion(region)
					.build();
		}

		// minio 등의 호환 S3 서비스를 사용하기 위해 커스텀 엔드포인트가 있는 경우 
		
		ClientConfiguration clientConfig = new ClientConfiguration();

		// HTTPS 프로토콜 사용시 인증서 오류 무시하도록 설정
		// TODO : 제대로된 PKI를 적용 
		SSLContext sslContext = null;
		try {
			sslContext = SSLContextBuilder.create()
					.loadTrustMaterial((chain, authType) -> true)
					.build();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		}
		ConnectionSocketFactory sslSocketFactory = new SdkTLSSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		clientConfig.getApacheHttpClientConfig().setSslSocketFactory(sslSocketFactory);
		
		return AmazonS3ClientBuilder.standard()
				.withClientConfiguration(clientConfig)
				//.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(customEndpoint, AwsHostNameUtils.parseRegion(customEndpoint, AmazonS3Client.S3_SERVICE_NAME)))
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(customEndpoint, Regions.EU_CENTRAL_1.getName()))
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withPathStyleAccessEnabled(true)
				.build();
	}
}
