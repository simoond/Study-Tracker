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

import React from "react";

export const keywordOptions = [
  {
    value: "PSC",
    label: "PSC",
    uri: "psc",
    type: "psc",
    color: "pink",
    badge: "badge-warning"
  },
  {
    value: "PSC Batch",
    label: "PSC Batch",
    uri: "pscbatch",
    type: "pscbatch",
    color: "blue-grey",
    badge: "badge-secondary"
  },
  {
    value: "PSC Diff Batch",
    label: "PSC Diff Batch",
    uri: "pscdiffbatch",
    type: "pscdiffbatch",
    color: "orange",
    badge: "badge-danger"
  },
  {
    value: "Gene",
    label: "Gene",
    uri: "genes",
    type: "gene",
    color: "light-green",
    badge: "badge-success"
  },
  {
    value: "qPCR Probe",
    label: "qPCR Probe",
    uri: "qpcr_probe",
    type: "qpcr_probe",
    color: "navy",
    badge: "badge-tertiary"
  },
  {
    value: "Primary Antibody",
    label: "Primary Antibody",
    uri: "p_antibody",
    type: "p_antibody",
    color: "purple",
    badge: "badge-info"
  }
];

export const findKeywordByValue = (value) => {
  let keyword = null;
  keywordOptions.forEach(kw => {
    if (kw.value === value) {
      keyword = kw;
    }
  });
  return keyword;
};

export const findKeywordByType = (type) => {
  let keyword = null;
  keywordOptions.forEach(kw => {
    if (kw.type === type) {
      keyword = kw;
    }
  });
  return keyword;
};

export const keywordOptionMap = (options => {
  let map = {};
  options.forEach(option => {
    map[option.value] = option;
  });
  return map;
})(keywordOptions);

