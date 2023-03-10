package com.lyugge.service.impl;

import com.lyugge.api.enums.Access;
import com.lyugge.api.enums.ExpirationTime;
import com.lyugge.api.request.RawPaste;
import com.lyugge.api.response.ResponsePaste;
import com.lyugge.dao.PasteDao;
import com.lyugge.entity.AppPaste;
import com.lyugge.exceptions.ExpirationTimeExceededException;
import com.lyugge.exceptions.PasteNotFoundException;
import com.lyugge.exceptions.WrongArgumentException;
import com.lyugge.service.PasteService;
import com.lyugge.utils.CryptoTool;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.lyugge.api.enums.Access.PUBLIC;

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
        var id = cryptoTool.idOf(hash);
        if (id == null) {
            throw new PasteNotFoundException(
                    String.format("Paste with hash %s has not found.", hash));
        }
        var paste = pasteDao.findById(id).
                orElseThrow(() -> new PasteNotFoundException(
                        String.format("Paste with hash %s has not found.", hash)));
        if (paste.getCancelDate().isAfter(LocalDateTime.now())) {
            return getResponsePaste(paste);
        } else {
            throw new ExpirationTimeExceededException(
                    String.format("Expiration time of paste with hash %s has exceeded.", hash));
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
        var listOfPastes = pasteDao.findAllByAccess(PUBLIC);
        int pastesLimit = 10;
        return listOfPastes.stream()
                .filter(appPaste -> appPaste.getCancelDate().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(AppPaste::getId).reversed())
                .limit(pastesLimit)
                .map(this::getResponsePaste)
                .collect(Collectors.toList());
    }

    @Override
    public String pushPaste(RawPaste rawPaste) {
        var transientPaste = getNewAppPaste(rawPaste);

        var persistentPaste = pasteDao.save(transientPaste);
        String hash = cryptoTool.hashOf(persistentPaste.getId());
        return serviceURL + hash;
    }

    private AppPaste getNewAppPaste(RawPaste rawPaste) {
        return AppPaste.builder()
                .text(rawPaste.getText())
                .cancelDate(getCancelDate(rawPaste.getTime()))
                .access(convertToAccess(rawPaste.getAccess()))
                .build();
    }

    private Access convertToAccess(String access) {
        Access eAccess;
        try {
            eAccess = Access.valueOf(access.toUpperCase());
        } catch(IllegalArgumentException e) {
            throw new WrongArgumentException("Wrong access argument, try " +
                    "PUBLIC or PRIVATE");
        }
        return eAccess;
    }

    private LocalDateTime getCancelDate(String time) {
        var now = LocalDateTime.of(LocalDate.now(), LocalTime.now());
        LocalDateTime cancelDate = null;

        ExpirationTime eTime = convertToExpirationTime(time);

        switch (eTime) {
            case TEN_MIN -> cancelDate = now.plusMinutes(10);
            case HOUR -> cancelDate = now.minusHours(1);
            case THREE_HOURS -> cancelDate = now.plusHours(3);
            case DAY -> cancelDate = now.plusDays(1);
            case WEEK -> cancelDate = now.plusWeeks(1);
            case MONTH -> cancelDate = now.plusMonths(1);
        }
        return cancelDate;
    }

    private ExpirationTime convertToExpirationTime(String time) {
        ExpirationTime eTime;
        try {
            eTime = ExpirationTime.valueOf(time.toUpperCase());
        } catch(IllegalArgumentException e) {
            throw new WrongArgumentException("Wrong time argument, try " +
                    "TEN_MIN, HOUR, THREE_HOURS, DAY, WEEK or MONTH");
        }
        return eTime;
    }
}