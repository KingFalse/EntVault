package com.cnwy.crawler.api;
import java.time.LocalDateTime;

import com.alibaba.fastjson2.JSON;
import com.cnwy.crawler.data.Enterprise;
import com.cnwy.crawler.data.EnterpriseRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


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
        enterpriseRepository.save(body);
        return ResponseEntity.of(Optional.of("OK"));
    }

}
