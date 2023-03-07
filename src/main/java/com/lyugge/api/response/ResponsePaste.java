package com.lyugge.api.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponsePaste {
    private String text;
    private String access;
}
