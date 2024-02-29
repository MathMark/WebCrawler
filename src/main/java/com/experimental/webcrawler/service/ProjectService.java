package com.experimental.webcrawler.service;

import com.experimental.webcrawler.exception.ProjectNotFoundException;
import com.experimental.webcrawler.model.WebsiteProject;
import com.experimental.webcrawler.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    
    public List<WebsiteProject> getAllProjects() {
        return projectRepository.findAll();
    }
    
    public WebsiteProject getById(String id) {
        Optional<WebsiteProject> projectOptional = projectRepository.findById(id);
        return projectOptional.orElseThrow(() -> 
                new ProjectNotFoundException(String.format("Project with id %s not found (it may not be created yet).", id)));
    }
}
