package com.pastelpunk.core.features.blobstorage;

import com.pastelpunk.core.config.model.BlobConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/container")
public class BlobController {

    private final BlobStorageService blobStorageService;

    public BlobController(BlobStorageService blobStorageService){
        this.blobStorageService = blobStorageService;
    }

    @PostMapping
    public void createContainer(@RequestBody String containerName){
        blobStorageService.createContainer(containerName);
    }

    @GetMapping("/{containerName}")
    public ResponseEntity<List<String>> getBlobs(@PathVariable String containerName){
        List<String> blobs = blobStorageService.listBlobs(containerName);
        return ResponseEntity.ok(blobs);
    }

    @PostMapping("/{containerName}")
    public ResponseEntity<String> createContainer(@PathVariable String containerName, @RequestBody String filePath){
        try {
            blobStorageService.uploadFile(containerName, new File(filePath));
            return ResponseEntity.ok().build();
        }catch (Exception e){
            log.error("Failed to upload file ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{containerName}/{blobName}")
    public ResponseEntity<String> downloadBlob(@PathVariable String containerName, @PathVariable String blobName){
        try {
            blobStorageService.getBlob(containerName, blobName, "/Users/gedison/IdeaProjects/blob-service/blob-service-core/src/main/resources/"+blobName);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            log.error("Failed to upload file ", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
