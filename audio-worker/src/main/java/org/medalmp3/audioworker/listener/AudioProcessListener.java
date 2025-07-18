package org.medalmp3.audioworker.listener;

import org.medalmp3.audioworker.config.RabbitConfig;
import org.medalmp3.audioworker.model.AudioRecord;
import org.medalmp3.audioworker.repository.AudioRecordRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Component
public class AudioProcessListener {
    private final S3Client s3;
    private final AudioRecordRepository repo;
    private final String bucket;

    public AudioProcessListener(S3Client s3,
                                AudioRecordRepository repo,
                                @Value("${aws.s3.bucket}") String bucket) {
        this.s3     = s3;
        this.repo   = repo;
        this.bucket = bucket;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handleAudioProcess(Long audioId) throws IOException {
        AudioRecord rec = repo.findById(audioId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID"));

        // 1) Download raw mp3
        byte[] mp3 = s3.getObjectAsBytes(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(rec.getS3Key())
                        .build())
                .asByteArray();

        // 2) TODO: process mp3 (transcode, waveform generation...)
        byte[] waveformPng = generateWaveform(mp3);

        // 3) Upload waveform image
        String waveformKey = "waveforms/" + UUID.randomUUID() + ".png";
        s3.putObject(PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(waveformKey)
                        .build(),
                RequestBody.fromBytes(waveformPng));

        // 4) Mark processed and save waveform key
        rec.setProcessed(true);
        rec.setWaveformKey(waveformKey);
        repo.save(rec);
    }

    private byte[] generateWaveform(byte[] mp3) {
        // placeholder: replace with real waveform logic
        return new byte[0];
    }
}