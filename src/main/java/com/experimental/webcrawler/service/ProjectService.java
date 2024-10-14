package com.experimental.webcrawler.service;

import com.experimental.webcrawler.exception.ProjectNotFoundException;
import com.experimental.webcrawler.model.document.WebsiteProjectDocument;
import com.experimental.webcrawler.repository.report.ReportDocumentRepository;
import com.experimental.webcrawler.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ReportDocumentRepository reportDocumentRepository;

    public List<WebsiteProjectDocument> getAllProjects() {
        return projectRepository.findAll();
    }

    public WebsiteProjectDocument getById(String id) {
        Optional<WebsiteProjectDocument> projectOptional = projectRepository.findById(id);
        return projectOptional.orElseThrow(() ->
                new ProjectNotFoundException(String.format("Project with id %s not found (it may not be created yet).", id)));
    }

    public void deleteProject(String id) {
//        Optional<WebsiteProjectDocument> projectOptional = projectRepository.findById(id);
//        if (projectOptional.isPresent()) {
//            Optional<BrokenPagesReportDocument> brokenPagesReportOptional = reportDocumentRepository.findByWebsiteProjectId(id);
//            brokenPagesReportOptional.ifPresent(brokenPagesReport -> reportDocumentRepository.deleteById(brokenPagesReport.getId()));
//            projectRepository.deleteById(id);
//        } else {
//            throw new ProjectNotFoundException(String.format("Project with id %s not found.", id));
//        }
    }
}
