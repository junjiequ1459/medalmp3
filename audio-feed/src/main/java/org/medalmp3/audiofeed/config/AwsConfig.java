package org.medalmp3.audiofeed.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;


@Configuration
public class AwsConfig {
    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public S3Presigner presigner() {
        return S3Presigner.builder()
                .region(Region.of(awsRegion))
                .build();
    }
}
