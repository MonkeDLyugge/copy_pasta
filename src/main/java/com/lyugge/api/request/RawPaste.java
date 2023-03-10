package com.lyugge.api.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawPaste {
    String text;
    String time;
    String access;
}
