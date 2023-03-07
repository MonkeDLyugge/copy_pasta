package com.lyugge.api.request;

import com.lyugge.api.enums.Access;
import com.lyugge.api.enums.ExpirationTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawPaste {
    String text;
    ExpirationTime time;
    Access access;
}
