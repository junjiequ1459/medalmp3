package org.medalmp3.audiofeed.controller;

import org.medalmp3.audiofeed.dto.AudioFeedItem;
import org.medalmp3.audiofeed.model.AudioRecord;
import org.medalmp3.audiofeed.repository.AudioRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feed")
public class FeedController {

    private final AudioRecordRepository repo;
    private final S3Presigner presigner;
    private final String bucket;

    public FeedController(AudioRecordRepository repo,
                          S3Presigner presigner,
                          @Value("${aws.s3.bucket}") String bucket) {
        this.repo      = repo;
        this.presigner = presigner;
        this.bucket    = bucket;
    }

    @GetMapping
    public List<AudioFeedItem> getProcessedFeed() {
        return repo.findByProcessedTrueOrderByUploadedAtDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private AudioFeedItem toDto(AudioRecord rec) {
        // build presigned URL to the waveform image
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(rec.getWaveformKey())
                .build();

        GetObjectPresignRequest preq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getReq)
                .build();

        String url = presigner.presignGetObject(preq)
                .url()
                .toString();

        return new AudioFeedItem(rec.getId(), rec.getUserId(), url);
    }
}
