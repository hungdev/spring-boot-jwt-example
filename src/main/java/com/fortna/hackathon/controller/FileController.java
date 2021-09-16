package com.fortna.hackathon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fortna.hackathon.dto.AppResponse;
import com.fortna.hackathon.service.FileService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@PreAuthorize("isAuthenticated()")
public class FileController {

    @Autowired
    private FileService fileService;

    @PreAuthorize("permitAll()")
    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("directory") String dir,
            @RequestParam("file") MultipartFile file) {
        fileService.storePlayerFile(dir, file);
        fileService.generateEntryPoint(dir);
        return ResponseEntity.status(HttpStatus.OK).body(new AppResponse(null, "Upload successfully!"));
    }

}
