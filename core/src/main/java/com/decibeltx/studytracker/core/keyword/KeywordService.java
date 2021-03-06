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

package com.decibeltx.studytracker.core.keyword;

import java.util.List;
import java.util.Optional;

public interface KeywordService {

  List<Keyword> findAll();

  Optional<Keyword> findByReferenceId(String referenceId);

  List<Keyword> findByKeyword(String keyword);

  List<Keyword> findBySource(String source);

  List<Keyword> findByType(String type);

  List<Keyword> search(String fragment);

  List<Keyword> search(String fragment, String type);

}
