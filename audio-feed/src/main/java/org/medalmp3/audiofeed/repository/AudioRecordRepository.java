package org.medalmp3.audiofeed.repository;

import org.medalmp3.audiofeed.model.AudioRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AudioRecordRepository extends JpaRepository<AudioRecord, Long> {
    List<AudioRecord> findByProcessedTrueOrderByUploadedAtDesc();
}
