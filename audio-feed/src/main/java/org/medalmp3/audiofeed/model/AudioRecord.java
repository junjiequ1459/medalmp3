package org.medalmp3.audiofeed.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "audio_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String s3Key;
    private Instant uploadedAt;
    private boolean processed;
    private String waveformKey;
}
