package com.hr.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hr.ppmtool.domain.Backlog;
import com.hr.ppmtool.domain.Project;
import com.hr.ppmtool.domain.User;
import com.hr.ppmtool.exceptions.ProjectIdException;
import com.hr.ppmtool.exceptions.ProjectNotFoundException;
import com.hr.ppmtool.repositories.BacklogRespository;
import com.hr.ppmtool.repositories.ProjectRepository;
import com.hr.ppmtool.repositories.UserRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private BacklogRespository backlogRespository;

	@Autowired

	private UserRepository userRepository;

	public Project saveOrUpdate(Project project, String username) {

		if (project.getId() != null) {
			Project existingProject = projectRepository.findByprojectId(project.getProjectId());

			if (existingProject != null && (!existingProject.getProjectLeader().equals(username))) {
				throw new ProjectNotFoundException("Project not Found in your account");
			} else if (existingProject == null) {
				throw new ProjectNotFoundException("Project with ID: '" + project.getProjectId()
						+ "' cannot be updated because it doesn't exists");
			}
		}

		try {

			User user = userRepository.findByUsername(username);
			project.setUser(user);
			project.setProjectLeader(user.getUsername());
			project.setProjectId(project.getProjectId().toUpperCase());
			if (project.getId() == null) {
				Backlog backlog = new Backlog();
				project.setBacklog(backlog);
				backlog.setProject(project);
				backlog.setProjectIdentifier(project.getProjectId().toUpperCase());
			}

			if (project.getId() != null) {
				project.setBacklog(backlogRespository.findByProjectIdentifier(project.getProjectId().toUpperCase()));
			}
			return projectRepository.save(project);

		} catch (Exception e) {
			throw new ProjectIdException("Project ID '" + project.getProjectId().toUpperCase() + "' Already Exists");
		}

	}

	public Project findProjectByIdentifier(String projectId, String username) {

		Project project = projectRepository.findByprojectId(projectId.toUpperCase());
		if (project == null) {
			throw new ProjectIdException("Project ID '" + projectId + "' does not exist");
		}

		if (!project.getProjectLeader().equals(username)) {
			throw new ProjectNotFoundException("Project Not found in your account");
		}

		return project;
	}

	public Iterable<Project> findAllProjects(String username) {
		return projectRepository.findAllByProjectLeader(username);
	}

	public void deleteProjectById(String projectId, String username) {

		projectRepository.delete(findProjectByIdentifier(projectId, username));

	}

}
