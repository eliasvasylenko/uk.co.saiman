import * as React from 'react';
import { ConsoleComponent, MapTable, ArrayTable } from 'app/sai-web-console';

export class Statline extends React.Component {
  render() {
    return <p export className="statline">React is working!!!</p>
  }
}

export class CommsInformation extends ConsoleComponent {
  render() {
    var entries = {
      name: "new",
      status: "3",
      channel: "hello there mate",
      bundle: "ok",
      actions: (
        <span>
          <button>start_polling</button>
          <button>open_connection</button>
          <button>close_connection</button>
        </span>
      )
    };
    return <MapTable header="info_header" entries={entries} />
  }
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

export class CommsTableHeader extends ConsoleComponent {
  render() {
    return <th export className="comms_header" id={this.props.id} >{this.props.id}</th>;
  }
}

export class CommsTable extends ConsoleComponent {
  render() {
    return (
      <div id="comms_table_container">
        <CommsTableControls />
        <ArrayTable
          columns={[
            "command_id",
            "command_output",
            "command_input",
            "command_actions"
          ]}
          rows={[
            {
              command_id: "first",
              command_output: "f_out",
              command_input: "f_in"
            },
            {
              command_id: "second",
              command_output: "f_out",
              command_actions: "actions!"
            }
          ]} />
      </div>
    );
  }
}

export class Comms extends ConsoleComponent {
  render() {
    return (
      <div id="comms">
        <CommsInformation />
        <Statline />
        <CommsTable />
      </div>
    )
  }
}
