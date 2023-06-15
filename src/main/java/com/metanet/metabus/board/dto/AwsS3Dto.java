package com.metanet.metabus.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AwsS3Dto {
    private String key;
    private String path;

    public AwsS3Dto() {

    }

    @Builder
    public AwsS3Dto(String key, String path) {
        this.key = key;
        this.path = path;
    }
}
