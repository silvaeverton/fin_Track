package com.everton.FinTrack.exceptions.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Error {
    private Integer status;
    private String message;
    private List<Fields> fields;



    @Getter
    @Builder
    public static class Fields {
        private String field;
        private String message;
    }

}
