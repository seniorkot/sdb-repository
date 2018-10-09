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

package ru.ifmo.se.sdbrep.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ifmo.se.sdbrep.model.Profile;
import ru.ifmo.se.sdbrep.model.Project;
import ru.ifmo.se.sdbrep.service.LogService;
import ru.ifmo.se.sdbrep.service.ProfileService;
import ru.ifmo.se.sdbrep.service.ProjectService;

/**
 * This class is RESTController for requests associated
 * with getting / creating / updating / deleting user
 * profile(s).
 *
 * @author seniorkot
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService mProfileService;

    @Autowired
    private ProjectService mProjectService;

    @Autowired
    private LogService mLogService;

    /**
     * This endpoint returns user profile specified by
     * username. If no username provided method returns
     * current user's profile.
     *
     * @param username Username (optional)
     * @return {@link Profile} entity and response code (200 - OK, 404 - User not found)
     */
    @RequestMapping(path = "/{username}", method = RequestMethod.GET)
    public ResponseEntity<Profile> getProfile(@PathVariable(required = false) String username) {
        Profile profile;
        if (username != null) {
            if ((profile = mProfileService.getByUsername(username)) != null) {
                return new ResponseEntity<>(profile, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        else {
            return new ResponseEntity<>(mProfileService.getCurrent(), HttpStatus.OK);
        }
    }

    /**
     * This endpoint updates current user's profile.
     *
     * @param profile Profile data to update
     * @return 200 - OK, 500 - Error
     */
    @RequestMapping(path = "/", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateProfile(@RequestBody Profile profile) {
        Profile currentProfile = mProfileService.update(profile);
        if (currentProfile != null) {
            mLogService.createLog("has updated profile info", currentProfile.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This endpoint creates new project in current
     * user's profile.
     *
     * @param projectName New project name
     * @return 201 - new project created, 400 - bad
     */
    @RequestMapping(path = "/project/{projectName}", method = RequestMethod.POST)
    public ResponseEntity<Void> createProject(@PathVariable String projectName) {
        Project project = mProjectService.create(projectName);
        if (project != null) {
            mLogService.createLog("has created project",
                    mProfileService.getCurrent().getId(), project.getId());
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}