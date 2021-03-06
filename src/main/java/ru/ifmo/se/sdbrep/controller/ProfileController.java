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
import ru.ifmo.se.sdbrep.model.Log;
import ru.ifmo.se.sdbrep.model.Profile;
import ru.ifmo.se.sdbrep.service.LogService;
import ru.ifmo.se.sdbrep.service.ProfileService;

import java.util.List;

/**
 * This class is a REST Controller for requests associated
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
    private LogService mLogService;

    /**
     * This endpoint returns current user's profile.
     *
     * @return 200 - OK
     */
    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseEntity<Profile> getCurrentProfile() {
        return new ResponseEntity<>(mProfileService.getCurrent(), HttpStatus.OK);
    }

    /**
     * This endpoint returns user profile by username.
     *
     * @param username Username
     * @return {@link Profile} entity and response code (200 - OK, 404 - User not found)
     */
    @RequestMapping(path = "/{username}", method = RequestMethod.GET)
    public ResponseEntity<Profile> getProfile(@PathVariable String username) {
        Profile profile;
        if ((profile = mProfileService.getByUsername(username)) != null) {
            return new ResponseEntity<>(profile, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * This endpoint updates current user's profile.
     *
     * @param profile Profile data to update
     * @return 200 - OK, 400 - Error
     */
    @RequestMapping(path = "", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateProfile(@RequestBody Profile profile) {
        Profile currentProfile = mProfileService.update(profile);
        if (currentProfile != null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * This endpoint returns current user's logs.
     *
     * @return 200 - OK
     */
    @RequestMapping(path = "/logs", method = RequestMethod.GET)
    public ResponseEntity<List<Log>> getProfileLogs() {
        List<Log> logs = mLogService.getAllByProfileId(mProfileService.getCurrent().getId());
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * This endpoint returns concrete user's logs.
     *
     * @param username Username
     * @return 200 - OK, 404 - User not found
     */
    @RequestMapping(path = "/{username}/logs", method = RequestMethod.GET)
    public ResponseEntity<List<Log>> getProfileLogs(@PathVariable String username) {
        Profile profile = mProfileService.getByUsername(username);
        if (profile == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Log> logs = mLogService.getAllByProfileId(profile.getId());
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
}
