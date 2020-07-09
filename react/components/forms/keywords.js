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

import React from 'react';
import {Col, FormGroup, Label, Row} from "reactstrap";
import Select from "react-select";
import {keywordOptionMap, keywordOptions} from "../../config/keywordConstants";
import {KeywordTypeBadge} from "../keywords";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimesCircle} from "@fortawesome/free-regular-svg-icons";
import AsyncSelect from "react-select/async";

export default class KeywordInputs extends React.Component {

  constructor(props) {
    super(props);
    this.state = {};
    this.handleCategoryChange = this.handleCategoryChange.bind(this);
    this.handleKeywordSelect = this.handleKeywordSelect.bind(this);
    this.keywordAutocomplete = this.keywordAutocomplete.bind(this);
    this.handleRemoveKeyword = this.handleRemoveKeyword.bind(this);
  }

  handleCategoryChange(selected) {
    console.log(selected);
    this.setState({
      category: selected.value
    })
  }

  handleRemoveKeyword(e) {
    const selected = e.currentTarget.dataset.id;
    const keywords = this.props.keywords.filter(keyword => {
      return (keyword.type + "-" + keyword.elasticSearchId) !== selected;
    });
    this.props.onChange({
      keywords: keywords
    });
  }

  handleKeywordSelect(selected) {
    console.log(selected);
    this.props.onChange({
      keywords: [
        ...this.props.keywords,
        selected
      ]
    })
  }

  getKeywordHits(json) {
    let options = [];
    if (json.hits.hits.length > 0) {
      json.hits.hits.forEach(hit => {
        switch (this.state.category) {

          case "PSC":
            options.push({
              elasticSearchId: hit._id,
              value: hit._source.doc.registry_id + ': '+hit._source.doc.name,
              item_id: hit._source.doc.registry_id,
              label: hit._source.doc.registry_id + ': '+hit._source.doc.name,
              type: 'psc',
              keyword: hit._source.doc.name,
              source: "TBD"
            });
            break;

          case "Cell Line":
            options.push({
              elasticSearchId: hit._id,
              value: hit._source.doc.name,
              item_id: hit._source.doc.item_id,
              label: hit._source.doc.name,
              type: 'cell_line',
              keyword: hit._source.doc.name,
              source: "TBD"
            });
            break;

          case "Gene":
            options.push({
              value: hit._source.doc.gene_id + hit._source.doc.synonyms
                  + hit._source.doc.name,
              label: hit._source.doc.symbol + ' : ' + hit._source.doc.organism,
              elasticSearchId: hit._id,
              type: 'gene',
              keyword: hit._source.doc.symbol + ' : '
                  + hit._source.doc.organism,
              source: "TBD"
            });
            break;

          case "PSC Batch":
            options.push({
              value: hit._source.doc.registry_id+ ' : '+hit._source.doc.name ,
              label: hit._source.doc.registry_id+ ' : '+hit._source.doc.name ,
              elasticSearchId: hit._id,
              type: 'pscbatch',
              keyword: hit._source.doc.registry_id+ ' : '+hit._source.doc.name ,
              source: "TBD"
            });
            break;

          case "PSC Diff Batch":
            options.push({
              value: hit._source.doc.registry_id + ' : '
                  + hit._source.doc.name,
              item_id: hit._source.doc.registry_id_id,
              label: hit._source.doc.registry_id + ' : '
                  + hit._source.doc.name,
              elasticSearchId: hit._id,
              type: 'pscdiffbatch',
              keyword: hit._source.doc.registry_id + ' : '
                  + hit._source.doc.name,
              source: "TBD"
            });
            break;

          case "qPCR Probe":
            options.push({
              value: hit._source.doc.name,
              item_id: hit._source.doc.registry_id,
              label: hit._source.doc.name,
              elasticSearchId: hit._id,
              type: 'qpcr_probe',
              keyword: hit._source.doc.name,
              source: "TBD"
            });
            break;

          case "Primary Antibody":
            options.push({
              value: hit._source.doc.name,
              label: hit._source.doc.name,
              elasticSearchId: hit._id,
              type: 'p_antibody',
              keyword: hit._source.doc.name,
              source: "TBD"
            });
            break;
        }
      });
    }
    return options;
  }

  keywordAutocomplete(input, callback) {
    console.log(input);
    if (input.length < 2) {
      return;
    }
    const uri = keywordOptionMap[this.state.category].uri;
    fetch(
        "https://vpc-fl60-dev-mdjua63vd64vr7tk66xfwn2ms4.us-east-1.es.amazonaws.com/"
        + uri + '/_search?q=*' + input + '*&size=50&pretty=true')
    .then(response => response.json())
    .then(json => {
      console.log(json);
      callback(this.getKeywordHits(json));
    }).catch(e => {
      console.error(e);
    })
  }

  render() {

    const selectedKeywords = this.props.keywords.map(keyword => {
      console.log(keyword);
      return (
          <Row
              key={"keyword-" + keyword.type + "-" + keyword.elasticSearchId}
              className="align-items-center justify-content-center mt-1"
          >
            <Col xs="3">
              <KeywordTypeBadge type={keyword.type}/>
            </Col>
            <Col xs="7">
              {keyword.keyword}
            </Col>
            <Col xs="2">
              <a onClick={this.handleRemoveKeyword}
                 data-id={keyword.type + "-" + keyword.elasticSearchId}>
                <FontAwesomeIcon
                    icon={faTimesCircle}
                    className="align-middle mr-2 text-danger"
                />
              </a>
            </Col>
          </Row>
      )
    });

    return (
        <Row form>
          <Col sm="2">
            <FormGroup>
              <Label>Category</Label>
              <Select
                  className="react-select-container"
                  classNamePrefix="react-select"
                  options={keywordOptions}
                  onChange={this.handleCategoryChange}
              />
            </FormGroup>
          </Col>
          <Col sm="5">
            <FormGroup>
              <Label>Keyword Search</Label>
              <AsyncSelect
                  placeholder={"Search-for and select keywords..."}
                  className={"react-select-container"}
                  classNamePrefix={"react-select"}
                  loadOptions={this.keywordAutocomplete}
                  onChange={this.handleKeywordSelect}
                  controlShouldRenderValue={false}
                  isDisabled={!this.state.category}
              />
            </FormGroup>
          </Col>
          <Col sm="5">
            <Row>
              <Col xs={12}>
                <Label>Selected</Label>
              </Col>
            </Row>
            {selectedKeywords}
          </Col>
        </Row>
    );

  }

}