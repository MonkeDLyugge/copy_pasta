package com.lyugge.controller;

import com.lyugge.api.request.RawPaste;
import com.lyugge.api.response.ResponsePaste;
import com.lyugge.service.PasteService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class PasteController {
    private final PasteService pasteService;

    public PasteController(PasteService pasteService) {
        this.pasteService = pasteService;
    }

    @GetMapping("/{hash}")
    public ResponsePaste getPaste(@PathVariable String hash) {
        return pasteService.getPaste(hash);
    }

    @GetMapping("/get_last_pastes")
    public ArrayList<String> getLastTenPastes() {
        return new ArrayList<>();
    }

    @PostMapping("/push")
    public String pushPaste(@RequestBody RawPaste rawPaste) {
        return rawPaste.getText();
    }
}
