package com.cnwy.crawler.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BeianRepository extends JpaRepository<Beian, Long>, JpaSpecificationExecutor<Beian> {

    Beian findByDomain(String domain);

}
