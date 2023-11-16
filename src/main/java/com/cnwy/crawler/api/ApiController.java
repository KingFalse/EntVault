package com.cnwy.crawler.api;

import com.cnwy.crawler.data.Enterprise;
import com.cnwy.crawler.data.EnterpriseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@RestController
public class ApiController {

    final
    EnterpriseRepository enterpriseRepository;

    public ApiController(EnterpriseRepository enterpriseRepository) {
        this.enterpriseRepository = enterpriseRepository;
    }

    @PostMapping("api/save/qcc")
    public ResponseEntity<String> save(@RequestBody Enterprise body) {
        body.setCreateTime(LocalDateTime.now());
        body.setUpdateTime(LocalDateTime.now());
        CompletableFuture.runAsync(() -> enterpriseRepository.save(body));
        return ResponseEntity.of(Optional.of("OK"));
    }

    @PostMapping("api/ent/existsByName")
    public Boolean existsByName(@RequestBody String name) {
        return enterpriseRepository.existsByName(name.strip());
    }

}
