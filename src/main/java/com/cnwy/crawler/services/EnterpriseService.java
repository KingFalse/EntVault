package com.cnwy.crawler.services;

import com.cnwy.crawler.data.Enterprise;
import com.cnwy.crawler.data.EnterpriseRepository;
import com.cnwy.crawler.data.SamplePerson;
import com.cnwy.crawler.data.SamplePersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EnterpriseService {

    private final EnterpriseRepository repository;

    public EnterpriseService(EnterpriseRepository repository) {
        this.repository = repository;
    }

    public Optional<Enterprise> get(Long id) {
        return repository.findById(id);
    }

    public Enterprise update(Enterprise entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Enterprise> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Enterprise> list(Pageable pageable, Specification<Enterprise> filter) {
        return repository.findAll(filter, pageable);
    }
    public Long count(Specification<Enterprise> filter) {
        return repository.count(filter);
    }

    public int count() {
        return (int) repository.count();
    }

}
