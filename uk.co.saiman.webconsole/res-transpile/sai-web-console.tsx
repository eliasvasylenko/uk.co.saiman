import * as React from 'react';

export class ConsoleComponent extends React.Component {
  componentDidMount() {
    initStaticWidgets();
  }

  componentDidUpdate() {
    initStaticWidgets();
  }
}

export var MapTableEntry = ({item, value}) => {
  return (
    <tr id="comms_table_container">
      <td id="item">{i18n[item]}</td>
      <td id="value">{value}</td>
    </tr>
  );
}

export var MapTable = ({header, entries}) => {
  return (
    <table id={header} export className="tablesorter nicetable noauto">
      <tr><th colSpan={2} export className="ui-widget-header">{i18n[header]}:</th></tr>
      {
        Object.keys(entries).map(key => (
          <MapTableEntry item={key} value={entries[key]} />
        ))
      }
    </table>
  );
}

export var ArrayTableHeader = ({columns}) => {
  return (
    <thead>
      <tr>
        {
          columns.map(column => (
            <th id={column}>{i18n[column]}</th>
          ))
        }
      </tr>
    </thead>
  );
}

export var ArrayTableRow = ({cell_data, columns}) => {
  return (
    <tr>
      {
        columns.map(column => (
          <td id={column}>{cell_data[column]}</td>
        ))
      }
    </tr>
  );
}

export var ArrayTable = ({rows, columns}) => {
  return (
    <table id="comms_table" export className="tablesorter nicetable noauto">
      <ArrayTableHeader columns={columns} />
      <tbody>
        {
          rows.map(row => (
            <ArrayTableRow data={row} columns={columns} />
          ))
        }
      </tbody>
    </table>
  );
}

export var TableControls = ({left, right}) => {
  return (
    <div id="table_controls" export className="ui-widget-header ui-corner-top buttonGroup">
      <span className="table_controls_left">{left}</span>
      <span className="table_controls_right">{right}</span>
    </div>
  );
}

export var FilterBox = ({callback}) => {
  return (
    <span>
      <input export className="filter" value="" title="{i18n.filter_help}"></input>
      <span export className="filterClear ui-icon ui-icon-close" title="{i18n.filter_clear}"></span>
      <button export className="filterButton" type="button" title="{i18n.filter_help}">{i18n.filter_apply}</button>
    </span>
  );
}
