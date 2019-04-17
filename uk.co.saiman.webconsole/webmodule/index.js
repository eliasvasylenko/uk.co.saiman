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

export const MapTableEntry = ({ item, value }) => (
  <tr>
    <td id="item">{item}</td>
    <td id="value">{value}</td>
  </tr>
)

export const MapTable = ({ header, entries, translate }) => (
  <table id={header} className="maptable tablesorter nicetable noauto">
    <thead>
      <tr><th colSpan={2} className="ui-widget-header">{translate(header)}:</th></tr>
    </thead>
    <tbody>
      {
        Object.keys(entries).map(entry => (
          <MapTableEntry key={entry} item={translate(entry)} value={entries[entry]} />
        ))
      }
    </tbody>
  </table>
)

export const ArrayTableHeader = ({ columns, translate }) => (
  <thead>
    <tr>
      {
        columns.map(column => (
          <th id={column} key={column}>{translate(column)}</th>
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

export const ArrayTable = ({ rows, columns, keyColumn, translate }) => (
  <table className="arrayTable tablesorter nicetable noauto">
    <ArrayTableHeader columns={columns} translate={translate} />
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

export const FilterBox = ({ filter, setFilter, clearFilter, translate }) => (
  <span>
    <input className="filter" value={filter} title={translate("filter.help")} onChange={e => setFilter(e.target.value)}></input>
    <span className="filterClear ui-icon ui-icon-close" title={translate("filter.clear")} onClick={e => clearFilter()}></span>
  </span>
)
