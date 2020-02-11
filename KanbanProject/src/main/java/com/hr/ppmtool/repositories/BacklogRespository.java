package com.hr.ppmtool.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hr.ppmtool.domain.Backlog;

@Repository
public interface BacklogRespository extends CrudRepository<Backlog, Long> {
	
	Backlog findByProjectIdentifier(String projectIdentifier);
	

}
