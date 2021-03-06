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
import {Media, Table} from 'reactstrap';
import {
  Bell,
  Edit,
  ExternalLink,
  File,
  FilePlus,
  FileText,
  Link,
  MessageCircle,
  Star,
  Trash2
} from 'react-feather';
import {studyActions} from "../config/activityConstants";
import {StatusBadge} from "./status";
import {KeywordBadgeList} from "./keywords";

const dateFormat = require('dateformat');

const createMarkup = (content) => {
  return {__html: content};
};

const ActivityIcon = ({action}) => {
  switch (action) {
    case studyActions.NEW_STUDY.value:
      return (
          <Star
              size={36}
              className="align-middle text-warning mr-4"
          />
      );
    case studyActions.UPDATED_STUDY.value:
      return (
          <Edit
              size={36}
              className="align-middle text-warning mr-4"
          />
      );
    case studyActions.DELETED_STUDY.value:
      return (
          <Trash2
              size={36}
              className="align-middle text-danger mr-4"
          />
      );
    case studyActions.STUDY_STATUS_CHANGED.value:
      return (
          <Bell
              size={36}
              className="align-middle text-info mr-4"
          />
      );
    case studyActions.FILE_UPLOADED.value:
      return (
          <FilePlus
              size={36}
              className="align-middle text-primary mr-4"
          />
      );
    case studyActions.NEW_STUDY_CONCLUSIONS.value:
      return (
          <FileText
              size={36}
              className="align-middle text-primary mr-4"
          />
      );
    case studyActions.EDITED_STUDY_CONCLUSIONS.value:
      return (
          <Edit
              size={36}
              className="align-middle text-warning mr-4"
          />
      );
    case studyActions.DELETED_STUDY_CONCLUSIONS.value:
      return (
          <Trash2
              size={36}
              className="align-middle text-danger mr-4"
          />
      );
    case studyActions.NEW_COMMENT.value:
      return (
          <MessageCircle
              size={36}
              className="align-middle text-info mr-4"
          />
      );
    case studyActions.EDITED_COMMENT.value:
      return (
          <MessageCircle
              size={36}
              className="align-middle text-info mr-4"
          />
      );
    case studyActions.DELETED_COMMENT.value:
      return (
          <Trash2
              size={36}
              className="align-middle text-danger mr-4"
          />
      );
    case studyActions.NEW_STUDY_RELATIONSHIP.value:
      return (
          <Link
              size={36}
              className="align-middle text-primary mr-4"
          />
      );
    case studyActions.UPDATED_STUDY_RELATIONSHIP.value:
      return (
          <Edit
              size={36}
              className="align-middle text-warning mr-4"
          />
      );
    case studyActions.DELETED_STUDY_RELATIONSHIP.value:
      return (
          <Trash2
              size={36}
              className="align-middle text-danger mr-4"
          />
      );
    case studyActions.NEW_STUDY_EXTERNAL_LINK.value:
      return (
          <ExternalLink
              size={36}
              className="align-middle text-primary mr-4"
          />
      );
    case studyActions.UPDATED_STUDY_EXTERNAL_LINK.value:
      return (
          <Edit
              size={36}
              className="align-middle text-warning mr-4"
          />
      );
    case studyActions.DELETED_STUDY_EXTERNAL_LINK.value:
      return (
          <Trash2
              size={36}
              className="align-middle text-danger mr-4"
          />
      );
    default:
      return (
          <Bell
              size={36}
              className="align-middle text-info mr-4"
          />
      );
  }
};

const ActivityMessage = ({activity}) => {
  switch (activity.action) {
    case studyActions.NEW_STUDY.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/users/"
              + activity.userAccountName}>{activity.userDisplayName}</a>
              &nbsp;has created a new study:
            </p>
            <div className="bg-light text-secondary p-3">
              <h5><a href={"/studies/"
              + activity.studyCode}>{activity.studyCode}</a></h5>
              <h3>{activity.data.name}</h3>
              <h5 className="text-muted">{activity.data.program.name}</h5>
              <div dangerouslySetInnerHTML={createMarkup(
                  activity.data.description)}/>
              <p>
                <KeywordBadgeList keywords={activity.data.keywords}/>
              </p>
            </div>
          </React.Fragment>
      );
    case studyActions.UPDATED_STUDY.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/users/"
              + activity.userAccountName}>{activity.userDisplayName}</a>
              &nbsp;has made an edit to a study:
            </p>
            <div className="bg-light text-secondary p-3">
              <h5><a href={"/studies/"
              + activity.studyCode}>{activity.studyCode}</a></h5>
              <h3>{activity.data.name}</h3>
              <h5 className="text-muted">{activity.data.program.name}</h5>
              <div dangerouslySetInnerHTML={createMarkup(
                  activity.data.description)}/>
              <p>
                <KeywordBadgeList keywords={activity.data.keywords}/>
              </p>
            </div>
          </React.Fragment>
      );
    case studyActions.DELETED_STUDY.value:
      return (
          <p>
            <a href={"/users/"
            + activity.userAccountName}>{activity.userDisplayName}</a>
            &nbsp;has removed study: {activity.studyCode}
          </p>
      );
    case studyActions.STUDY_STATUS_CHANGED.value:
      return (
          <p>
            <a href={"/users/"
            + activity.userAccountName}>{activity.userDisplayName}</a>
            &nbsp;has updated the status of study&nbsp;
            <a href={"/studies/" + activity.studyCode}>{activity.studyCode}</a>
            &nbsp;to&nbsp;
            <StatusBadge status={activity.data}/>
          </p>
      );
    case studyActions.FILE_UPLOADED.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/users/"
              + activity.userAccountName}>{activity.userDisplayName}</a>
              &nbsp;has attached a new file to study:&nbsp;
              <a href={"/studies/"
              + activity.studyCode}>{activity.studyCode}</a>
            </p>
            <div className="bg-light text-secondary p-3">
              <h3>
                <a href={activity.data.url} target="_blank">
                  <File size={24}/>
                  &nbsp;
                  {activity.data.filename}
                </a>
              </h3>
            </div>
          </React.Fragment>

      );
    case studyActions.NEW_STUDY_CONCLUSIONS.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/users/"
              + activity.userAccountName}>{activity.userDisplayName}</a>
              &nbsp;has added new conclusions for study:&nbsp;
              <a href={"/studies/"
              + activity.studyCode}>{activity.studyCode}</a>
            </p>
            <div className="bg-light font-italic text-secondary p-3"
                 dangerouslySetInnerHTML={createMarkup(activity.data.content)}/>
          </React.Fragment>
      );
    case studyActions.EDITED_STUDY_CONCLUSIONS.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/users/"
              + activity.userAccountName}>{activity.userDisplayName}</a>
              &nbsp;has updated the conclusions for study:&nbsp;
              <a href={"/studies/"
              + activity.studyCode}>{activity.studyCode}</a>
            </p>
            <div className="bg-light font-italic text-secondary p-3"
                 dangerouslySetInnerHTML={createMarkup(activity.data.content)}/>
          </React.Fragment>

      );
    case studyActions.DELETED_STUDY_CONCLUSIONS.value:
      return (
          <p>
            <a href={"/users/"
            + activity.userAccountName}>{activity.userDisplayName}</a>
            &nbsp;has removed the conclusions for study:&nbsp;
            <a href={"/studies/" + activity.studyCode}>{activity.studyCode}</a>
          </p>
      );
    case studyActions.NEW_COMMENT.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/users/"
              + activity.userAccountName}>{activity.userDisplayName}</a>
              &nbsp;has posted a new comment to study&nbsp;
              <a href={"/studies/"
              + activity.studyCode}>{activity.studyCode}</a>:
            </p>
            <p className="bg-light font-italic text-secondary p-3">
              "{activity.data.text}"
            </p>
          </React.Fragment>

      );
    case studyActions.EDITED_COMMENT.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/users/"
              + activity.userAccountName}>{activity.userDisplayName}</a>
              &nbsp;has edited their comment to study:&nbsp;
              <a href={"/studies/"
              + activity.studyCode}>{activity.studyCode}</a>
            </p>
            <p className="bg-light font-italic text-secondary p-3">
              "{activity.data.text}"
            </p>
          </React.Fragment>
      );
    case studyActions.DELETED_COMMENT.value:
      return (
          <p>
            <a href={"/users/"
            + activity.userAccountName}>{activity.userDisplayName}</a>
            &nbsp;has removed their comment to study:&nbsp;
            <a href={"/studies/" + activity.studyCode}>{activity.studyCode}</a>
          </p>
      );
    case studyActions.NEW_STUDY_RELATIONSHIP.value:
      return (
          <p>
            <a href={"/users/"
            + activity.userAccountName}>{activity.userDisplayName}</a>
            &nbsp;has added a new study relationship for study:&nbsp;
            <a href={"/studies/" + activity.studyCode}>{activity.studyCode}</a>
          </p>
      );
    case studyActions.UPDATED_STUDY_RELATIONSHIP.value:
      return (
          <p>
            <a href={"/users/"
            + activity.userAccountName}>{activity.userDisplayName}</a>
            &nbsp;has updated a study relationship for study:&nbsp;
            <a href={"/studies/" + activity.studyCode}>{activity.studyCode}</a>
          </p>
      );
    case studyActions.DELETED_STUDY_RELATIONSHIP.value:
      return (
          <p>
            <a href={"/users/"
            + activity.userAccountName}>{activity.userDisplayName}</a>
            &nbsp;has removed a study relationship for study:&nbsp;
            <a href={"/studies/" + activity.studyCode}>{activity.studyCode}</a>
          </p>
      );
    case studyActions.NEW_STUDY_EXTERNAL_LINK.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/users/"
              + activity.userAccountName}>{activity.userDisplayName}</a>
              &nbsp;has added a new external link for study:&nbsp;
              <a href={"/studies/"
              + activity.studyCode}>{activity.studyCode}</a>
            </p>
            <p className="bg-light text-secondary p-3">
              <Link size={16}/>
              &nbsp;
              <a href={activity.data.url}
                 target="_blank">{activity.data.label}</a>
            </p>
          </React.Fragment>
      );
    case studyActions.UPDATED_STUDY_EXTERNAL_LINK.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/users/"
              + activity.userAccountName}>{activity.userDisplayName}</a>
              &nbsp;has edited an external link for study:&nbsp;
              <a href={"/studies/"
              + activity.studyCode}>{activity.studyCode}</a>
            </p>
            <p className="bg-light text-secondary p-3">
              <Link size={16}/>
              &nbsp;
              <a href={activity.data.url}
                 target="_blank">{activity.data.label}</a>
            </p>
          </React.Fragment>
      );
    case studyActions.DELETED_STUDY_EXTERNAL_LINK.value:
      return (
          <p>
            <a href={"/users/"
            + activity.userAccountName}>{activity.userDisplayName}</a>
            &nbsp;has removed an external link for study:&nbsp;
            <a href={"/studies/" + activity.studyCode}>{activity.studyCode}</a>
          </p>
      );
    default:
      return (
          <p>
            {activity.userDisplayName}
          </p>
      );
  }
};

export const StudyTimelineActivity = ({activity}) => {
  return (
      <Media>

        <ActivityIcon action={activity.action}/>

        <Media body>

          <small className="float-right text-navy">
            {dateFormat(new Date(activity.date), 'mm/dd/yy @ h:MM TT')}
          </small>

          <p className="mb-2">
            <strong>
              {
                studyActions.hasOwnProperty(activity.action)
                    ? studyActions[activity.action].label
                    : "New Activity"
              }
            </strong>
          </p>

          <ActivityMessage activity={activity}/>

        </Media>
      </Media>
  )
};

export const Timeline = ({activities}) => {
  console.log(activities);
  if (!activities || activities.length === 0) {
    return <p>Oops..</p>;
  }
  let flag = false;
  const elements = activities
  .filter(a => studyActions[a.action].visible)
  .sort((a, b) => {
    if (a.date > b.date) {
      return -1;
    } else if (a.date < b.date) {
      return 1;
    } else {
      return 0;
    }
  })
  .map(a => {
    let hr = '';
    if (flag) {
      hr = <hr/>;
    }
    flag = true;
    return (
        <React.Fragment key={'activity-' + a.id}>
          {hr}
          <StudyTimelineActivity activity={a}/>
        </React.Fragment>
    );
  });
  return (
      <React.Fragment>
        {elements}
      </React.Fragment>
  );

};

export const ActivityTable = ({activity}) => {
  const rows = activity
  .sort((a, b) => {
    if (a.date > b.date) {
      return -1;
    } else if (a.date < b.date) {
      return 1;
    } else {
      return 0;
    }
  })
  .map(a => {
    return (
        <tr key={'activity-' + a.date}>
          <td>{a.userDisplayName}</td>
          <td>{a.action}</td>
          <td>{new Date(a.date).toLocaleString()}</td>
        </tr>
    );
  });
  return (
      <Table striped>
        <thead>
        <tr>
          <th>User</th>
          <th>Action</th>
          <th>Date</th>
        </tr>
        </thead>
        <tbody>
        {rows}
        </tbody>
      </Table>
  )
};