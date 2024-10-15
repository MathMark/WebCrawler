package com.seo;

import com.seo.model.document.WebsiteProjectDocument;
import com.seo.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "PROJECTS",
        description = "REST API for projects."
)
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(
            summary = "Get all projects",
            description = "REST API to get all created projects"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @GetMapping
    public ResponseEntity<List<WebsiteProjectDocument>> getAllProjects() {
        List<WebsiteProjectDocument> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @Operation(
            summary = "Get project by id",
            description = "REST API to get a project by its id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @GetMapping("/{projectId}")
    public ResponseEntity<WebsiteProjectDocument> getById(@PathVariable String projectId) {
        WebsiteProjectDocument project = projectService.getById(projectId);
        return ResponseEntity.ok(project);
    }

    @Operation(
            summary = "Delete project by id",
            description = "REST API to delete a project by its id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String projectId) {
        projectService.deleteProject(projectId);
    }
}
