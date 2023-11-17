package com.cnwy.crawler.services;

import com.cnwy.crawler.data.AssociateGW;
import com.cnwy.crawler.data.AssociateGWRepository;
import com.cnwy.crawler.data.Enterprise;
import com.cnwy.crawler.data.EnterpriseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AssociateGWService {

    private final AssociateGWRepository repository;

    public AssociateGWService(AssociateGWRepository repository) {
        this.repository = repository;
    }

    public Optional<AssociateGW> get(Long id) {
        return repository.findById(id);
    }

    public AssociateGW update(AssociateGW entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<AssociateGW> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<AssociateGW> list(Pageable pageable, Specification<AssociateGW> filter) {
        return repository.findAll(filter, pageable);
    }
    public Long count(Specification<AssociateGW> filter) {
        return repository.count(filter);
    }

    public int count() {
        return (int) repository.count();
    }

}
