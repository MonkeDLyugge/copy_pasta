package com.lyugge.service.impl;

import com.lyugge.dao.PasteDao;
import com.lyugge.entity.AppPaste;
import com.lyugge.api.request.RawPaste;
import com.lyugge.api.response.ResponsePaste;
import com.lyugge.service.PasteService;
import com.lyugge.utils.CryptoTool;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j
@Service
public class PasteServiceImpl implements PasteService {
    private final CryptoTool cryptoTool;
    private final PasteDao pasteDao;

    public PasteServiceImpl(CryptoTool cryptoTool, PasteDao pasteDao) {
        this.cryptoTool = cryptoTool;
        this.pasteDao = pasteDao;
    }

    @Override
    public ResponsePaste getPaste(String hash) {
        long id = cryptoTool.idOf(hash);
        var paste = pasteDao.findById(id);
        if (paste.isPresent()) {
            return getResponsePaste(paste.get());
        } else {
            //TODO process that paste may not be found
            return ResponsePaste.builder().build();
        }
    }

    private ResponsePaste getResponsePaste(AppPaste paste) {
        return ResponsePaste.builder()
                .access(paste.getAccess())
                .text(paste.getText())
                .build();
    }

    @Override
    public List<ResponsePaste> getLastTenPastes() {
        return null;
    }

    @Override
    public String pushPaste(RawPaste rawPaste) {
        return null;
    }
}
