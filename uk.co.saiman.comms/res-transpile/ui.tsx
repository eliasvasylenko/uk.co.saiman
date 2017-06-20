import * as React from 'react';
import { ConsoleComponent, MapTable, ArrayTable, StatLine } from 'app/sai-web-console';

export const CommsInformation = ({name, connection, bundle}) => {
  var entries = {
    name,
    status: connection.status,
    channel: connection.channel,
    bundle: (
      <a href="/system/console/bundles/{this.props.bundle.id}">
        {bundle.name}
        <span className="symName">{bundle.symbolicName}</span>
      </a>
    ),
    actions: (
      <span>
        <button>start_polling</button>
        <button>stop_polling</button>
        <button>open_connection</button>
        <button>close_connection</button>
      </span>
    )
  };
  return (
    <div id="comms_information_container">
      <Statline status="status" />
      <MapTable header="info_header" entries={entries} />
    </div>
  );
}

export class CommsTableControls extends ConsoleComponent {
  render() {
    return (
      <div id="filter_container" export className="ui-widget-header ui-corner-top buttonGroup">
        <input export className="filter" value="" title="{i18n.filter_help}"></input>
        <span export className="filterClear ui-icon ui-icon-close" title="{i18n.filter_clear}"></span>
        <button export className="filterButton" type="button" title="{i18n.filter_help}">{i18n.filter_apply}</button>
        <button export className="pollButton" type="button"></button>
      </div>
    )
  }
}

export const CommsTable = ({commands}) => (
  <div id="comms_table_container">
    <Statline status="status" />
    <TableControls
      left = {
        <FilterBox />
      }
      right = {
        <button>Start polling</button>
      } />
    <ArrayTable
      columns={[
        "command_id",
        "command_output",
        "command_input",
        "command_actions"
      ]}
      rows={commands} />
  </div>
)

export const Comms = ({bundle, commands}) => (
  <div id="comms">
    <CommsInformation bundle />
    <CommsTable commands />
  </div>
)

export class CommsContainer extends ConsoleComponent {
  render() {
    return <Comms bundle={state.bundle} commands={state.commands} />
  }
}
