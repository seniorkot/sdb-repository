/*
 * MIT License
 *
 * Copyright (c) 2018 seniorkot
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.ifmo.se.sdbrep.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.ifmo.se.sdbrep.model.Profile;
import ru.ifmo.se.sdbrep.model.Project;
import ru.ifmo.se.sdbrep.repository.ProjectRepository;
import ru.ifmo.se.sdbrep.service.LogService;
import ru.ifmo.se.sdbrep.service.ProfileService;
import ru.ifmo.se.sdbrep.service.ProjectService;

import java.util.List;
import java.util.Optional;

/**
 * This class is used as project app service
 * that implements {@link ProjectService} methods.
 *
 * @author seniorkot
 * @version 1.0
 * @since 1.0
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository mProjectRepository;

    @Autowired
    private ProfileService mProfileService;

    @Autowired
    private LogService mLogService;

    @Override
    public Project getById(@NonNull String id) {
        Optional<Project> project = mProjectRepository.findById(id);
        return project.orElse(null);
    }

    @Override
    public Project getCurrentByName(@NonNull String name) {
        Profile profile = mProfileService.getCurrent();
        for (Project project : profile.getProjects()) {
            if (project != null && project.getName().equals(name)) {
                return project;
            }
        }
        return null;
    }

    @Override
    public Project getByProfileUsernameAndName(@NonNull String username, @NonNull String name) {
        Profile profile = mProfileService.getByUsername(username);
        for (Project project : profile.getProjects()) {
            if (project != null && project.getName().equals(name)) {
                return project;
            }
        }
        return null;
    }

    @Override
    public List<Project> getAllCurrent() {
        Profile profile = mProfileService.getCurrent();
        return profile.getProjects();
    }

    @Override
    public List<Project> getAllByProfileUsername(String username) {
        Profile profile = mProfileService.getByUsername(username);
        return profile.getProjects();
    }

    @Override
    public List<Project> getAllByCollaborator(@NonNull String collaborator) {
        return mProjectRepository.findAllByCollaboratorsContains(collaborator);
    }

    @Override
    public Project create(@NonNull String name) {
        if (getCurrentByName(name) == null) {
            Project project = new Project();
            project.setName(name);
            project = mProjectRepository.insert(project);
            mProfileService.addProject(project);
            mLogService.createLog("Has created project",
                    mProfileService.getCurrent().getId(), project.getId());
            return project;
        }
        return null;
    }

    @Override
    public Project update(@NonNull String projectName, @NonNull Project project) {
        Project selectedProject = getCurrentByName(projectName);
        if (selectedProject != null) {
            if (project.getName() != null) {
                if (getCurrentByName(project.getName()) == null) {
                    selectedProject.setName(project.getName());
                }
                else {
                    return null;
                }
            }
            if (project.getInfo() != null) {
                selectedProject.setInfo(project.getInfo());
            }
            mLogService.createLog("Has updated project",
                    mProfileService.getCurrent().getId(), project.getId());
            return mProjectRepository.save(selectedProject);
        }
        return null;
    }

    @Override
    public void delete(Project project) {
        mProfileService.removeProject(project);
        mProjectRepository.delete(project);
        mLogService.createLog("Has deleted project",
                mProfileService.getCurrent().getId(), project.getId());
    }

    @Override
    public Project addCollaborator(@NonNull String projectName, @NonNull String collaboratorName) {
        Project project = getCurrentByName(projectName);
        Profile collaborator = mProfileService.getByUsername(collaboratorName);
        if (project != null && collaborator != null) {
            if (!project.getCollaborators().contains(collaborator) &&
                    !mProfileService.getCurrent().equals(collaborator)) {
                project.getCollaborators().add(collaborator);
                mLogService.createLog("Has added " + collaboratorName + " as collaborator",
                        mProfileService.getCurrent().getId(), project.getId());
                return mProjectRepository.save(project);
            }
            return null;
        }
        return null;
    }

    @Override
    public Project removeCollaborator(@NonNull String projectName, @NonNull String collaboratorName) {
        Project project = getCurrentByName(projectName);
        Profile collaborator = mProfileService.getByUsername(collaboratorName);
        if (project != null && collaborator != null && project.getCollaborators().contains(collaborator)) {
            project.getCollaborators().remove(collaborator);
            mLogService.createLog("Has removed " + collaboratorName + " from collaborators",
                    mProfileService.getCurrent().getId(), project.getId());
            return mProjectRepository.save(project);
        }
        return null;
    }
}
