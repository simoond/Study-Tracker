/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.web.controller.api;

import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api/study")
public class StudyBaseController extends StudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyBaseController.class);

  @GetMapping("")
  public List<Study> getAllStudies(
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "owner", required = false) String owner,
      @RequestParam(value = "user", required = false) String userId,
      @RequestParam(value = "active", defaultValue = "false") boolean active,
      @RequestParam(value = "legacy", defaultValue = "false") boolean legacy,
      @RequestParam(value = "external", defaultValue = "false") boolean external,
      @RequestParam(value = "my", defaultValue = "false") boolean my,
      @RequestParam(value = "search", required = false) String search
  ) {

    // Search
    if (!StringUtils.isEmpty(search)) {
      return getStudyService().search(search);
    }

    // Find by owner
    else if (owner != null) {
      Optional<User> optional = getUserService().findById(owner);
      if (!optional.isPresent()) {
        throw new RecordNotFoundException("Cannot find user record: " + owner);
      }
      return getStudyService().findAll()
          .stream()
          .filter(study -> study.getOwner().equals(owner) && study.isActive())
          .collect(Collectors.toList());
    }

    // Find by user TODO
    else if (userId != null) {
      Optional<User> optional = getUserService().findById(userId);
      if (!optional.isPresent()) {
        throw new RecordNotFoundException("Cannot find user record: " + userId);
      }
      return getStudyService().findAll()
          .stream()
          .filter(study -> study.getOwner().equals(optional.get().getId()) && study.isActive())
          .collect(Collectors.toList());
    }

    // My studies TODO
    else if (my) {
      try {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User user = getUserService().findByAccountName(userDetails.getUsername())
            .orElseThrow(RecordNotFoundException::new);
        return getStudyService().findAll().stream()
            .filter(s -> s.getOwner().equals(user))
            .collect(Collectors.toList());
      } catch (Exception e) {
        throw new StudyTrackerException(e);
      }
    }

    // Active
    else if (active) {
      return getStudyService().findAll()
          .stream()
          .filter(study -> study.isActive() && Arrays.asList(Status.IN_PLANNING, Status.ACTIVE)
              .contains(study.getStatus()))
          .collect(Collectors.toList());
    }

    // Legacy
    else if (legacy) {
      return getStudyService().findAll().stream()
          .filter(s -> s.isLegacy() && s.isActive())
          .collect(Collectors.toList());
    } else if (external) {
      return getStudyService().findAll().stream()
          .filter(s -> s.getCollaborator() != null)
          .collect(Collectors.toList());
    }

    // Find by code
    else if (code != null) {
      return Collections.singletonList(
          getStudyService().findByCode(code).orElseThrow(RecordNotFoundException::new));
    }

    // Find all
    else {
      return getStudyService().findAll().stream().filter(Study::isActive)
          .collect(Collectors.toList());
    }
  }

  @GetMapping("/{id}")
  public Study getStudy(@PathVariable("id") String studyId) throws RecordNotFoundException {
    return getStudyFromIdentifier(studyId);
  }

  @PostMapping("")
  public HttpEntity<Study> createStudy(@RequestBody Study study) {

    LOGGER.info("Creating study");
    LOGGER.info(study.toString());

    // Get authenticated user
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    User user = getUserService().findByAccountName(userDetails.getUsername())
        .orElseThrow(RecordNotFoundException::new);
    study.setCreatedBy(user);

    // Save the record
    getStudyService().create(study);
    Assert.notNull(study.getId(), "Study not persisted.");

    return new ResponseEntity<>(study, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<Study> updateStudy(@PathVariable("id") String id, @RequestBody Study study) {
    LOGGER.info("Updating study");
    LOGGER.info(study.toString());
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    User user = getUserService().findByAccountName(userDetails.getUsername())
        .orElseThrow(RecordNotFoundException::new);
    study.setLastModifiedBy(user);
    getStudyService().update(study);
    return new ResponseEntity<>(study, HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteStudy(@PathVariable("id") String id) {
    LOGGER.info("Deleting study: " + id);
    Study study = getStudyFromIdentifier(id);
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    User user = getUserService().findByAccountName(userDetails.getUsername())
        .orElseThrow(RecordNotFoundException::new);
    study.setLastModifiedBy(user);
    getStudyService().delete(study);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{id}/status")
  public void updateStudyStatus(@PathVariable("id") String id,
      @RequestBody Map<String, Object> params) throws StudyTrackerException {
    if (!params.containsKey("status")) {
      throw new StudyTrackerException("No status label provided.");
    }
    Study study = getStudyFromIdentifier(id);
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    User user = getUserService().findByAccountName(userDetails.getUsername())
        .orElseThrow(RecordNotFoundException::new);
    study.setLastModifiedBy(user);
    String label = (String) params.get("status");
    Status status = Status.valueOf(label);
    LOGGER.info(String.format("Setting status of study %s to %s", id, label));
    getStudyService().updateStatus(study, status);
  }

}
