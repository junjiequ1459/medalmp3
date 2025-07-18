package org.medalmp3.audiofeed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AudioFeedItem {
    private Long id;
    private String userId;
    private String waveformUrl;
}
