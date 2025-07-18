package org.medalmp3.audioapi.dto;

public class UploadResponse {
    private Long id;
    private String s3Key;

    public UploadResponse() { }

    public UploadResponse(Long id, String s3Key) {
        this.id    = id;
        this.s3Key = s3Key;
    }

    public Long getId() {
        return id;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }
}
