package org.medalmp3.audioapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioRecord {
    @Id
    @GeneratedValue
    private Long id;
    private String userId;
    private String s3Key;
    private Instant uploadedAt;
    private boolean processed = false;
    private String waveformUrl;
}
