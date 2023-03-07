package com.lyugge.service;

import com.lyugge.api.request.RawPaste;
import com.lyugge.api.response.ResponsePaste;
import java.util.List;

public interface PasteService {
    ResponsePaste getPaste(String hash);

    List<ResponsePaste> getLastTenPastes();

    String pushPaste(RawPaste rawPaste);
}
