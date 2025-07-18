package org.medalmp3.audioapi.dto;

public class AudioInfoResponse {
    private Long    id;
    private String  s3Key;
    private boolean processed;
    private String  presignedUrl;

    public AudioInfoResponse() { }

    public AudioInfoResponse(Long id, String s3Key, boolean processed, String presignedUrl) {
        this.id           = id;
        this.s3Key        = s3Key;
        this.processed    = processed;
        this.presignedUrl = presignedUrl;
    }

    public Long getId() {
        return id;
    }

    public String getS3Key() {
        return s3Key;
    }

    public boolean isProcessed() {
        return processed;
    }

    public String getPresignedUrl() {
        return presignedUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public void setPresignedUrl(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }
}
