package org.medalmp3.audioapi.controller;

import org.medalmp3.audioapi.config.RabbitConfig;
import org.medalmp3.audioapi.dto.AudioInfoResponse;
import org.medalmp3.audioapi.dto.UploadResponse;
import org.medalmp3.audioapi.model.AudioRecord;
import org.medalmp3.audioapi.repository.AudioRecordRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/audio")
public class AudioController {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AudioRecordRepository repo;
    private final RabbitTemplate rabbits;
    private final String bucket;

    public AudioController(S3Client s3Client,
                           S3Presigner s3Presigner,
                           AudioRecordRepository repo,
                           RabbitTemplate rabbits,
                           @Value("${aws.s3.bucket}") String bucket) {
        this.s3Client    = s3Client;
        this.s3Presigner = s3Presigner;
        this.repo        = repo;
        this.rabbits     = rabbits;
        this.bucket      = bucket;
    }

    @PostMapping
    public ResponseEntity<UploadResponse> uploadAudio(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId
    ) throws IOException {
        String key = "audio/" + UUID.randomUUID() + ".mp3";

        // 1) upload to S3
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        // 2) save metadata
        AudioRecord rec = repo.save(new AudioRecord(
                null, userId, key, Instant.now(), false, null
        ));

        // 3) publish to RabbitMQ
        rabbits.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                rec.getId()
        );

        return ResponseEntity.ok(new UploadResponse(rec.getId(), key));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AudioInfoResponse> getAudioInfo(
            @PathVariable("id") Long id
    ) {
        return repo.findById(id)
                .map(rec -> {
                    GetObjectRequest getReq = GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(rec.getS3Key())
                            .build();

                    GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                            .getObjectRequest(getReq)
                            .signatureDuration(Duration.ofMinutes(15))
                            .build();

                    PresignedGetObjectRequest presigned =
                            s3Presigner.presignGetObject(presignReq);

                    AudioInfoResponse dto = new AudioInfoResponse(
                            rec.getId(),
                            rec.getS3Key(),
                            rec.isProcessed(),
                            presigned.url().toString()
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
