package org.prezrohit.securefilestorage.controllers;

import org.prezrohit.securefilestorage.exceptions.StorageFileNotFoundException;
import org.prezrohit.securefilestorage.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;

@Controller
public class FileUploadController {
    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @ResponseBody
    @GetMapping("files/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        Resource file = storageService.loadAsResource(fileName);

        if (file == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ResponseBody
    @PostMapping("/")
    public ResponseEntity<String> handleFileUpload(@RequestParam("files") MultipartFile[] files) {
        Arrays.stream(files).forEach(storageService::store);
        return ResponseEntity.ok("Files uploaded successfully!");
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFoundException(StorageFileNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    // will be used later to show UI with download button
    @ResponseBody
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileName) {
        // Path to the file to be downloaded
        File file = new File(fileName);

        // Prepare HTML content
        String htmlContent = "<html><body>"
                + "<h1>Download Your File</h1>"
                + "<p>Click the link below to download the file:</p>"
                + "<a href='/file/" + fileName + "'>Download File</a>"
                + "</body></html>";

        // Create headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.setContentType(MediaType.TEXT_HTML);

        // Return a combined response
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(htmlContent);
    }
}
