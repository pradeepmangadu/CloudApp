package com.hr.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hr.ppmtool.domain.Backlog;
import com.hr.ppmtool.domain.ProjectTask;
import com.hr.ppmtool.exceptions.ProjectNotFoundException;
import com.hr.ppmtool.repositories.BacklogRespository;
import com.hr.ppmtool.repositories.ProjectRepository;
import com.hr.ppmtool.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {

	@Autowired
	BacklogRespository backlogRespository;

	@Autowired
	ProjectTaskRepository projectTaskRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	private ProjectService projectService;

	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {

		// PTs to be added to a specific project,project !=null,BL exists

		Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();// backlogRespository.findByProjectIdentifier(projectIdentifier);
		// set BL to Project
		projectTask.setBacklog(backlog);
		// we want our project sequence to be like IDPRO-1 IDPRO-2
		Integer BacklogSequence = backlog.getPTSequence();
		// Update the BL SEQUENCE
		BacklogSequence++;

		backlog.setPTSequence(BacklogSequence);
		// Add Sequence to Project Task
		projectTask.setProjectSequence(backlog.getProjectIdentifier() + "-" + BacklogSequence);
		projectTask.setProjectId(projectIdentifier);
		// Initial Priority when priority null

		if (projectTask.getPriority() == null || projectTask.getPriority() == 0) { // In the future we need to add this
																					// condition
			// projectTask.getPriority()==0||
			projectTask.setPriority(3);
		}

		// INITIAL status when status is null
		if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
			projectTask.setStatus("TO_DO");
		}
		return projectTaskRepository.save(projectTask);

	}

	public Iterable<ProjectTask> findBacklogById(String Id, String username) {
		projectService.findProjectByIdentifier(Id, username);
		return projectTaskRepository.findByProjectIdOrderByPriority(Id);
	}

	public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id, String username) {
		// Make Sure we are searching for existing backlog
		projectService.findProjectByIdentifier(backlog_id, username);
		// Make Sure that Project Task Exists
		ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
		if (projectTask == null) {
			throw new ProjectNotFoundException("ProjectTask with SequenceID:'" + pt_id + "' doesn't exists");
		}
		// Make sure that backlog Id /ProjectId corresponds to the project task
		if (!projectTask.getProjectId().equals(backlog_id)) {
			throw new ProjectNotFoundException(
					"Project Task '" + pt_id + "' does not exists in project: '" + backlog_id);
		}
		return projectTask;
	}

	public ProjectTask updateByProjectSequence(ProjectTask updatedtTask, String backlog_id, String pt_id,
			String username) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
		projectTask = updatedtTask;

		return projectTaskRepository.save(projectTask);
	}

	public void deletebyProjectSequence(String backlog_id, String pt_id, String username) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
		projectTaskRepository.delete(projectTask);
	}
}
