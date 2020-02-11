package com.hr.ppmtool.web;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr.ppmtool.domain.Project;
import com.hr.ppmtool.services.MapValidationErrorService;
import com.hr.ppmtool.services.ProjectService;

@RestController
@RequestMapping("/api/project")
@CrossOrigin // Added to Integrate SpringBoot with REACT
public class ProjectController {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private MapValidationErrorService mapValidationErrorService;

	@PostMapping("")
	public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, BindingResult result,
			Principal principal) {

		ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
		if (errorMap != null)
			return errorMap;

		Project project1 = projectService.saveOrUpdate(project, principal.getName());
		return new ResponseEntity<Project>(project, HttpStatus.CREATED);
	}

	@GetMapping("/{projectId}")
	public ResponseEntity<?> getProjectById(@PathVariable String projectId, Principal principal) {
		Project project = projectService.findProjectByIdentifier(projectId, principal.getName());
		return new ResponseEntity<Project>(project, HttpStatus.OK);

	}

	@GetMapping("/all")
	public Iterable<Project> getAllProjects(Principal principal) {
		return projectService.findAllProjects(principal.getName());
	}

	@DeleteMapping("/{projectId}")
	public ResponseEntity<?> deleteProjectById(@PathVariable String projectId, Principal principal) {
		projectService.deleteProjectById(projectId, principal.getName());
		return new ResponseEntity<String>("Project with ID '" + projectId + "' is deleted", HttpStatus.OK);
	}

}
