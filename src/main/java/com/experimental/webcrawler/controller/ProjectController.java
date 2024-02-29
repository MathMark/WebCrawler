package com.experimental.webcrawler.controller;

import com.experimental.webcrawler.model.WebsiteProject;
import com.experimental.webcrawler.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectService projectService;
    
    @GetMapping("/all")
    public ResponseEntity<List<WebsiteProject>> getAllProjects() {
        List<WebsiteProject> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping
    public ResponseEntity<WebsiteProject> getById(@RequestParam String projectId) {
        WebsiteProject project = projectService.getById(projectId);
        return ResponseEntity.ok(project);
    }
}
