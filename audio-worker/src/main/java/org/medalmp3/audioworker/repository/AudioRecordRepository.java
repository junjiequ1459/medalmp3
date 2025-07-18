package org.medalmp3.audioworker.repository;

import org.medalmp3.audioworker.model.AudioRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioRecordRepository extends JpaRepository<AudioRecord, Long> {
}