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

package com.decibeltx.studytracker.core.storage;

import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.storage.exception.StudyStorageDuplicateException;
import com.decibeltx.studytracker.core.storage.exception.StudyStorageException;
import com.decibeltx.studytracker.core.storage.exception.StudyStorageNotFoundException;
import com.decibeltx.studytracker.core.storage.exception.StudyStorageWriteException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFileSystemStudyStorageService implements StudyStorageService {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(LocalFileSystemStudyStorageService.class);

  private final Path rootPath;
  private final Path rootUrl = Paths.get("/static");
  private boolean overwriteExisting = false;
  private boolean useExisting = false;

  public LocalFileSystemStudyStorageService(Path rootPath) {
    this.rootPath = rootPath;
  }

  /**
   * Returns an absolute URL for the target storage item, given an absolute file system {@link
   * Path}.
   *
   * @param path
   * @return
   */
  private String getObjectUrl(Path path) {
    Path relative = rootPath.relativize(path);
    return rootUrl.resolve(relative).toString();
  }

  /**
   * Returns {@link StorageFile} instances for every file in the target folder, at the top level.
   *
   * @param path
   * @return
   */
  private List<StorageFile> getFolderFiles(Path path) {
    try {
      return Files.walk(path, 1)
          .filter(Files::isRegularFile)
          .map(f -> {
            BasicStorageFile file = new BasicStorageFile();
            file.setPath(f);
            file.setName(f.getFileName().toString());
            file.setUrl(getObjectUrl(f));
            return file;
          })
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new StudyTrackerException(e);
    }
  }

  /**
   * Returns {@link StorageFolder} instances for every subfolder in the target folder.
   *
   * @param path
   * @return
   */
  private List<StorageFolder> getSubfolders(Path path) {
    try {
      return Files.walk(path, 1)
          .filter(Files::isDirectory)
          .map(d -> {
            BasicStorageFolder folder = new BasicStorageFolder();
            folder.setName(d.toFile().getName());
            folder.setPath(d);
            folder.setUrl(getObjectUrl(d));
            return folder;
          })
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new StudyTrackerException(e);
    }
  }

  @Override
  public StorageFolder getProgramFolder(Program program) throws StudyStorageNotFoundException {
    Path path = rootPath.resolve(StorageUtils.getProgramFolderName(program));
    File file = path.toFile();
    if (!file.isDirectory() || !file.exists()) {
      throw new StudyStorageNotFoundException(program.getName());
    }
    BasicStorageFolder folder = new BasicStorageFolder();
    folder.setPath(path);
    folder.setName(file.getName());
    folder.setUrl(getObjectUrl(path));
    folder.setFiles(getFolderFiles(path));
    folder.setSubFolders(getSubfolders(path));
    return folder;
  }

  @Override
  public StorageFolder getStudyFolder(Study study) throws StudyStorageNotFoundException {
    LOGGER.info("Fetching storage folder instance for study: " + study.getCode());
    StorageFolder programFolder = this.getProgramFolder(study.getProgram());
    Path programPath = Paths.get(programFolder.getPath());
    Path studyFolder = programPath.resolve(StorageUtils.getStudyFolderName(study));
    LOGGER.debug(studyFolder.toString());
    File file = studyFolder.toFile();
    if (!file.isDirectory() || !file.exists()) {
      throw new StudyStorageNotFoundException(study.getCode());
    }
    BasicStorageFolder folder = new BasicStorageFolder();
    folder.setPath(studyFolder);
    folder.setName(file.getName());
    folder.setUrl(getObjectUrl(studyFolder));
    folder.setFiles(getFolderFiles(studyFolder));
    folder.setSubFolders(getSubfolders(studyFolder));
    return folder;
  }

  @Override
  public StorageFolder getAssayFolder(Assay assay) throws StudyStorageNotFoundException {
    LOGGER.info("Fetching storage folder instance for assay: " + assay.getCode());
    StorageFolder studyFolder = this.getStudyFolder(assay.getStudy());
    Path studyPath = Paths.get(studyFolder.getPath());
    Path assayFolder = studyPath.resolve(StorageUtils.getAssayFolderName(assay));
    File file = assayFolder.toFile();
    if (!file.isDirectory() || !file.exists()) {
      throw new StudyStorageNotFoundException(assay.getCode());
    }
    BasicStorageFolder folder = new BasicStorageFolder();
    folder.setPath(assayFolder);
    folder.setName(file.getName());
    folder.setUrl(getObjectUrl(assayFolder));
    folder.setFiles(getFolderFiles(assayFolder));
    folder.setSubFolders(getSubfolders(assayFolder));
    return folder;
  }

  @Override
  public StorageFolder createProgramFolder(Program program) throws StudyStorageException {
    LOGGER.info("Creating storage folder instance for program: " + program.getName());
    String folderName = StorageUtils.getProgramFolderName(program);
    Path programPath = rootPath.resolve(folderName);
    File newFolder = programPath.toFile();
    if (newFolder.exists()) {
      if (useExisting) {
        LOGGER.info("Using existing folder.");
      } else if (overwriteExisting) {
        LOGGER.info("Overwriting existing folder");
        boolean success = newFolder.delete();
        if (!success) {
          throw new StudyStorageWriteException(
              "Failed to delete program folder: " + newFolder.getAbsolutePath());
        }
      } else {
        throw new StudyStorageDuplicateException(
            "Program folder already exists: " + newFolder.getAbsolutePath());
      }
    } else {
      boolean success = newFolder.mkdir();
      if (!success) {
        throw new StudyStorageWriteException(
            "Failed to create program folder: " + newFolder.getAbsolutePath());
      }
    }
    StorageFolder folder = new BasicStorageFolder();
    folder.setName(newFolder.getName());
    folder.setPath(programPath);
    folder.setUrl(getObjectUrl(programPath));
    return folder;
  }

  @Override
  public StorageFolder createStudyFolder(Study study) throws StudyStorageException {
    LOGGER.info("Creating storage folder instance for study: " + study.getCode());
    StorageFolder programFolder = this.getProgramFolder(study.getProgram());
    Path programPath = Paths.get(programFolder.getPath());
    Path studyPath = programPath.resolve(StorageUtils.getStudyFolderName(study));
    File newFolder = studyPath.toFile();
    if (newFolder.exists()) {
      if (useExisting) {
        LOGGER.info("Using existing folder.");
      } else if (overwriteExisting) {
        LOGGER.info("Overwriting existing folder");
        boolean success = newFolder.delete();
        if (!success) {
          throw new StudyStorageWriteException(
              "Failed to delete study folder: " + newFolder.getAbsolutePath());
        }
      } else {
        throw new StudyStorageDuplicateException(
            "Study folder already exists: " + newFolder.getAbsolutePath());
      }
    } else {
      boolean success = newFolder.mkdir();
      if (!success) {
        throw new StudyStorageWriteException(
            "Failed to create study folder: " + newFolder.getAbsolutePath());
      }
    }
    StorageFolder folder = new BasicStorageFolder();
    folder.setName(newFolder.getName());
    folder.setPath(studyPath);
    folder.setUrl(getObjectUrl(studyPath));
    return folder;

  }

  @Override
  public StorageFolder createAssayFolder(Assay assay) throws StudyStorageException {
    LOGGER.info("Creating storage folder instance for assay: " + assay.getCode());
    StorageFolder studyFolder = this.getStudyFolder(assay.getStudy());
    Path studyPath = Paths.get(studyFolder.getPath());
    Path assayPath = studyPath.resolve(StorageUtils.getAssayFolderName(assay));
    File newFolder = assayPath.toFile();
    if (newFolder.exists()) {
      if (useExisting) {
        LOGGER.info("Using existing folder.");
      } else if (overwriteExisting) {
        LOGGER.info("Overwriting existing folder");
        boolean success = newFolder.delete();
        if (!success) {
          throw new StudyStorageWriteException(
              "Failed to delete assay folder: " + newFolder.getAbsolutePath());
        }
      } else {
        throw new StudyStorageDuplicateException(
            "Assay folder already exists: " + newFolder.getAbsolutePath());
      }
    } else {
      boolean success = newFolder.mkdir();
      if (!success) {
        throw new StudyStorageWriteException(
            "Failed to create assay folder: " + newFolder.getAbsolutePath());
      }
    }
    StorageFolder folder = new BasicStorageFolder();
    folder.setName(newFolder.getName());
    folder.setPath(assayPath);
    folder.setUrl(getObjectUrl(assayPath));
    return folder;

  }

  @Override
  public StorageFile saveStudyFile(File file, Study study) throws StudyStorageException {
    LOGGER.info(String.format("Saving file %s to storage folder instance for study %s",
        file.getName(), study.getCode()));
    StorageFolder studyFolder = this.getStudyFolder(study);
    Path studyPath = Paths.get(studyFolder.getPath());
    Path newFilePath = studyPath.resolve(file.getName());
    File newFile = newFilePath.toFile();
    try {
      FileUtils.copyFile(file, newFile);
    } catch (Exception e) {
      throw new StudyTrackerException(e);
    }
    StorageFile studyFile = new BasicStorageFile();
    studyFile.setPath(newFilePath);
    studyFile.setUrl(getObjectUrl(newFilePath));
    return studyFile;
  }

  @Override
  public StorageFile saveAssayFile(File file, Assay assay) throws StudyStorageException {
    LOGGER.info(String.format("Saving file %s to storage folder instance for assay %s",
        file.getName(), assay.getCode()));
    StorageFolder assayFolder = this.getAssayFolder(assay);
    Path assayPath = Paths.get(assayFolder.getPath());
    Path newFilePath = assayPath.resolve(file.getName());
    File newFile = newFilePath.toFile();
    try {
      FileUtils.copyFile(file, newFile);
    } catch (Exception e) {
      throw new StudyTrackerException(e);
    }
    StorageFile assayFile = new BasicStorageFile();
    assayFile.setPath(newFilePath);
    assayFile.setUrl(getObjectUrl(newFilePath));
    return assayFile;
  }

  public void setOverwriteExisting(boolean overwriteExisting) {
    this.overwriteExisting = overwriteExisting;
  }

  public void setUseExisting(boolean useExisting) {
    this.useExisting = useExisting;
  }
}
