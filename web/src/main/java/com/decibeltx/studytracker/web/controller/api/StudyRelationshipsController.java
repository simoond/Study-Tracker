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
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.StudyRelationship;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.StudyRelationshipService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/study/{id}/relationships")
@RestController
public class StudyRelationshipsController extends StudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyRelationshipsController.class);

  @Autowired
  private StudyRelationshipService studyRelationshipService;

  @GetMapping("")
  public List<StudyRelationship> getStudyRelationships(@PathVariable("id") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    return studyRelationshipService.getStudyRelationships(study);
  }

  @PostMapping("")
  public HttpEntity<?> createStudyRelationship(@PathVariable("id") String sourceStudyId,
      @RequestBody StudyRelationship studyRelationship) {
    LOGGER
        .info(String.format("Creating new study relationship for study %s: type=%s targetStudy=%s",
            sourceStudyId, studyRelationship.getType(), studyRelationship.getStudyId()));
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    User user = getUserService().findByAccountName(userDetails.getUsername())
        .orElseThrow(RecordNotFoundException::new);
    Study sourceStudy = getStudyFromIdentifier(sourceStudyId);
    sourceStudy.setLastModifiedBy(user);
    Study targetStudy = getStudyFromIdentifier(studyRelationship.getStudyId());
    targetStudy.setLastModifiedBy(user);
    studyRelationshipService
        .addStudyRelationship(sourceStudy, targetStudy, studyRelationship.getType());
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @DeleteMapping("")
  public HttpEntity<?> deleteStudyRelationship(@PathVariable("id") String sourceStudyId,
      @RequestBody StudyRelationship studyRelationship) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    User user = getUserService().findByAccountName(userDetails.getUsername())
        .orElseThrow(RecordNotFoundException::new);
    Study sourceStudy = getStudyFromIdentifier(sourceStudyId);
    sourceStudy.setLastModifiedBy(user);
    Study targetStudy = getStudyFromIdentifier(studyRelationship.getStudyId());
    targetStudy.setLastModifiedBy(user);
    studyRelationshipService.removeStudyRelationship(sourceStudy, targetStudy);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
