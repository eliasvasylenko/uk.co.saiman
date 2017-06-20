import * as React from 'react';

export class ConsoleComponent extends React.Component {
  componentDidMount() {
    initStaticWidgets();
  }

  componentDidUpdate() {
    initStaticWidgets();
  }
}

export const StatLine = ({status}) => <p export className="statline">{status}</p>

export const MapTableEntry = ({item, value}) => (
  <tr id="comms_table_container">
    <td id="item">{i18n[item]}</td>
    <td id="value">{value}</td>
  </tr>
)

export const MapTable = ({header, entries}) => (
  <table id={header} export className="tablesorter nicetable noauto">
    <tr><th colSpan={2} export className="ui-widget-header">{i18n[header]}:</th></tr>
    {
      Object.keys(entries).map(key => (
        <MapTableEntry item={key} value={entries[key]} />
      ))
    }
  </table>
)

export const ArrayTableHeader = ({columns}) => (
  <thead>
    <tr>
      {
        columns.map(column => (
          <th id={column}>{i18n[column]}</th>
        ))
      }
    </tr>
  </thead>
)

export const ArrayTableRow = ({cell_data, columns}) => (
  <tr>
    {
      columns.map(column => (
        <td id={column}>{cell_data[column]}</td>
      ))
    }
  </tr>
)

export const ArrayTable = ({rows, columns}) => (
  <table id="comms_table" export className="tablesorter nicetable noauto">
    <ArrayTableHeader columns={columns} />
    <tbody>
      {
        rows.map(row => (
          <ArrayTableRow data={row} columns />
        ))
      }
    </tbody>
  </table>
)

export const TableControls = ({left, right}) => (
  <div id="table_controls" export className="ui-widget-header ui-corner-top buttonGroup">
    <span className="table_controls_left">{left}</span>
    <span className="table_controls_right">{right}</span>
  </div>
)

export const FilterBox = ({callback}) => (
  <span>
    <input export className="filter" value="" title="{i18n.filter_help}"></input>
    <span export className="filterClear ui-icon ui-icon-close" title="{i18n.filter_clear}"></span>
    <button export className="filterButton" type="button" title="{i18n.filter_help}">{i18n.filter_apply}</button>
  </span>
)
