package com.lyugge.utils;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CryptoTool {
    private final Hashids hashids;

    @Value("${crypto_tool.salt}")
    private String salt;

    public CryptoTool() {
        int minHashLength = 10;
        this.hashids = new Hashids(salt, minHashLength);
    }

    public String hashOf(Long value) {
        return hashids.encode(value);
    }

    public Long idOf(String value) {
        long[] res = hashids.decode(value);
        if (res != null && res.length > 0) {
            return res[0];
        }
        return null;
    }
}
