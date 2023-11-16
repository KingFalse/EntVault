package com.cnwy.crawler.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BeianRepository extends JpaRepository<Beian, Long>, JpaSpecificationExecutor<Beian> {

    Optional<Beian> findByDomain(String domain);

}
