import * as React from 'react';

export class ConsoleComponent extends React.Component {
  componentDidMount() {
    initStaticWidgets();
  }

  componentDidUpdate() {
    initStaticWidgets();
  }
}

export const StatLine = ({ status }) => <p className="statline">{status}</p>

export const MapTableEntry = ({item, value}) => (
  <tr>
    <td id="item">{i18n[item]}</td>
    <td id="value">{value}</td>
  </tr>
)

export const MapTable = ({ header, entries }) => (
  <table id={header} className="maptable tablesorter nicetable noauto">
    <thead>
      <tr><th colSpan={2} className="ui-widget-header">{i18n[header]}:</th></tr>
    </thead>
    <tbody>
      {
        Object.keys(entries).map(entry => (
          <MapTableEntry key={entry} item={entry} value={entries[entry]} />
        ))
      }
    </tbody>
  </table>
)

export const ArrayTableHeader = ({ columns }) => (
  <thead>
    <tr>
      {
        columns.map(column => (
          <th id={column} key={column}>{i18n[column]}</th>
        ))
      }
    </tr>
  </thead>
)

export const ArrayTableRow = ({ data, columns, id }) => (
  <tr id={id}>
    {
      columns.map(column => { return (
        <td id={column} key={column}>{data[column]}</td>
      )})
    }
  </tr>
)

export const ArrayTable = ({ rows, columns, keyColumn }) => (
  <table className="arrayTable tablesorter nicetable noauto">
    <ArrayTableHeader columns={columns} />
    <tbody>
      {
        rows.map(row => (
          <ArrayTableRow data={row} columns={columns} key={row[keyColumn]} id={row[keyColumn]} />
        ))
      }
    </tbody>
  </table>
)

export const TableControls = ({ left, right }) => (
  <div className="tableControls ui-widget-header ui-corner-top buttonGroup">
    <span className="tableControlsLeft">{left}</span>
    <span className="tableControlsRight">{right}</span>
  </div>
)

export const FilterBox = ({ filter, setFilter, clearFilter }) => (
  <span>
    <input className="filter" value={filter} title={i18n.filter_help} onChange={e => setFilter(e.target.value)}></input>
    <span className="filterClear ui-icon ui-icon-close" title={i18n.filter_clear} onClick={e => clearFilter()}></span>
  </span>
)
