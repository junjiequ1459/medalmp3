package org.medalmp3.audioapi.repository;

import org.medalmp3.audioapi.model.AudioRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AudioRecordRepository
        extends JpaRepository<AudioRecord, Long> {
    List<AudioRecord> findTop50ByUserIdAndProcessedTrueOrderByUploadedAtDesc(String userId);
}
