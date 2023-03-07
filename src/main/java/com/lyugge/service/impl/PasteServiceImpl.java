package com.lyugge.service.impl;

import com.lyugge.api.enums.ExpirationTime;
import com.lyugge.dao.PasteDao;
import com.lyugge.entity.AppPaste;
import com.lyugge.api.request.RawPaste;
import com.lyugge.api.response.ResponsePaste;
import com.lyugge.service.PasteService;
import com.lyugge.utils.CryptoTool;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

import static com.lyugge.api.enums.Access.*;
import static com.lyugge.api.enums.ExpirationTime.*;

@Log4j
@Service
public class PasteServiceImpl implements PasteService {
    private final CryptoTool cryptoTool;
    private final PasteDao pasteDao;
    @Value("${service.url}")
    private String serviceURL;

    public PasteServiceImpl(CryptoTool cryptoTool, PasteDao pasteDao) {
        this.cryptoTool = cryptoTool;
        this.pasteDao = pasteDao;
    }

    @Override
    public ResponsePaste getPaste(String hash) {
        long id = cryptoTool.idOf(hash);
        var paste = pasteDao.findById(id);
        if (paste.isPresent()) {
            //TODO check cancelDate
            return getResponsePaste(paste.get());
        } else {
            //TODO process that paste may not be found
            log.error("Can't find paste with hash: " + hash);
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
        var transientPaste = getNewAppPaste(rawPaste);
        var persistentPaste = pasteDao.save(transientPaste);
        String hash = cryptoTool.hashOf(persistentPaste.getId());
        String uri = serviceURL + hash;
        return uri;
    }

    private AppPaste getNewAppPaste(RawPaste rawPaste) {
        return AppPaste.builder()
                .text(rawPaste.getText())
                .cancelDate(getCancelDate(rawPaste.getTime()))
                .access(rawPaste.getAccess())
                .build();
    }

    private String getCancelDate(ExpirationTime time) {
        var now = LocalDateTime.of(LocalDate.now(), LocalTime.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime cancelDate = null;
        switch(time) {
            case TEN_MIN: {
                cancelDate = now.plusMinutes(10);
                break;
            }
            case HOUR: {
                cancelDate = now.minusHours(1);
                break;
            }
            case THREE_HOURS: {
                cancelDate = now.plusHours(3);
                break;
            }
            case DAY: {
                cancelDate = now.plusDays(1);
                break;
            }
            case WEEK: {
                cancelDate = now.plusWeeks(1);
                break;
            }
            case MONTH: {
                cancelDate = now.plusMonths(1);
                break;
            }
        }
        if (cancelDate == null) {
            //TODO check if time is not in enum
            log.error("Can't serialize time paste need to save");
        }
        return cancelDate.format(formatter);
    }
}
//    String str = "1986-04-08 12:30";
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
//    Date nowDate = new Date();
//    Date prevDate = new Date(nowDate.getTime() - 86400000);
//    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//    String nowDay = dateFormat.format(nowDate);
//    String prevDay = dateFormat.format(prevDate);