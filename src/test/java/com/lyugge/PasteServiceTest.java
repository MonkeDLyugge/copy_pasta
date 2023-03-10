package com.lyugge;

import com.lyugge.api.enums.Access;
import com.lyugge.api.request.RawPaste;
import com.lyugge.dao.PasteDao;
import com.lyugge.entity.AppPaste;
import com.lyugge.exceptions.ExpirationTimeExceededException;
import com.lyugge.exceptions.PasteNotFoundException;
import com.lyugge.exceptions.WrongArgumentException;
import com.lyugge.service.impl.PasteServiceImpl;
import com.lyugge.utils.CryptoTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class PasteServiceTest extends UnitTest {
    @Autowired
    private PasteDao pasteDao;

    @Autowired
    private CryptoTool cryptoTool;

    @Autowired
    private PasteServiceImpl pasteService;

    @Test
    void pushPaste() {
        var wrongAccessPaste = RawPaste.builder()
                .text("some paste with wrong access")
                .access("protected")
                .time("day")
                .build();
        var wrongTimePaste = RawPaste.builder()
                .text("some paste with wrong time")
                .access("private")
                .time("four_week")
                .build();

        Assertions.assertThrows(WrongArgumentException.class,
                () -> pasteService.pushPaste(wrongAccessPaste));
        Assertions.assertThrows(WrongArgumentException.class,
                () -> pasteService.pushPaste(wrongTimePaste));
    }

    @Test
    void getPaste() {
        var appPaste = AppPaste.builder()
                .text("some paste")
                .access(Access.PRIVATE)
                .cancelDate(LocalDateTime.now().plusDays(1))
                .build();
        var persistentPaste = pasteDao.save(appPaste);
        var hash = cryptoTool.hashOf(persistentPaste.getId());
        var resPaste = pasteService.getPaste(hash);
        Assertions.assertEquals(resPaste.getAccess(), appPaste.getAccess());
        Assertions.assertEquals(resPaste.getText(), appPaste.getText());

        var wrongAppPaste = AppPaste.builder()
                .text("some paste with expired time")
                .access(Access.PRIVATE)
                .cancelDate(LocalDateTime.now().minusHours(1))
                .build();
        var persistentPasteExpTime = pasteDao.save(wrongAppPaste);
        var wrongHash = cryptoTool.hashOf(persistentPasteExpTime.getId());
        Assertions.assertThrows(ExpirationTimeExceededException.class,
                () -> pasteService.getPaste(wrongHash));

        var notFoundHash = "5fs87s6dfg";
        Assertions.assertThrows(PasteNotFoundException.class,
                () -> pasteService.getPaste(notFoundHash));
    }
}
