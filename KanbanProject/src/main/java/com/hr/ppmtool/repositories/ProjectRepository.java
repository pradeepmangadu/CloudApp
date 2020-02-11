package com.hr.ppmtool.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hr.ppmtool.domain.Project;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

	@Override
	Iterable<Project> findAll();

	Project findByprojectId(String projectId);

	Iterable<Project> findAllByProjectLeader(String username);

}
