package com.cnwy.crawler.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AssociateGWRepository extends JpaRepository<AssociateGW, Long>, JpaSpecificationExecutor<AssociateGW> {

}
