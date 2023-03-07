package com.lyugge.api.request;

import com.lyugge.api.enums.Access;
import com.lyugge.api.enums.ExpirationTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RawPaste {
    String text;
    ExpirationTime time;
    Access access;
}
