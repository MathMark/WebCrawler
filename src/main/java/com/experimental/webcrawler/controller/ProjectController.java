package com.experimental.webcrawler.controller;

import com.experimental.webcrawler.model.WebsiteProjectDocument;
import com.experimental.webcrawler.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectService projectService;
    
    @GetMapping("/all")
    public ResponseEntity<List<WebsiteProjectDocument>> getAllProjects() {
        List<WebsiteProjectDocument> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping
    public ResponseEntity<WebsiteProjectDocument> getById(@RequestParam String projectId) {
        WebsiteProjectDocument project = projectService.getById(projectId);
        return ResponseEntity.ok(project);
    }
    
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam String projectId) {
        projectService.deleteProject(projectId);
    }
}
