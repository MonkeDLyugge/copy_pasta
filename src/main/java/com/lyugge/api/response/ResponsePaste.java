package com.lyugge.api.response;

import com.lyugge.api.enums.Access;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePaste {
    private String text;
    private Access access;
}
