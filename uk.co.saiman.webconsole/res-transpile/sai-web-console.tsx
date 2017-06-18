import * as React from 'react';

export class ConsoleComponent extends React.Component {
  componentDidMount() {
    initStaticWidgets();
  }

  componentDidUpdate() {
    initStaticWidgets();
  }
}

export class MapTableEntry extends ConsoleComponent {
  render() {
    return (
      <tr id="comms_table_container">
        <td id="item">{i18n[this.props.item]}</td>
        <td id="value">{this.props.value}</td>
      </tr>
    );
  }
}

export class MapTable extends ConsoleComponent {
  render() {
    return (
      <table id={this.props.header} export className="tablesorter nicetable noauto">
        <tr><th colSpan={2} export className="ui-widget-header">{i18n[this.props.header]}:</th></tr>
        {
          Object.keys(this.props.entries).map(key => (
            <MapTableEntry item={key} value={this.props.entries[key]} />
          ))
        }
      </table>
    );
  }
}

export class ArrayTableHeader extends ConsoleComponent {
  render() {
    return (
      <thead>
        <tr>
          {
            this.props.columns.map(column => (
              <th id={column}>{i18n[column]}</th>
            ))
          }
        </tr>
      </thead>
    );
  }
}

export class ArrayTableRow extends ConsoleComponent {
  render() {
    return (
      <tr>
        {
          this.props.columns.map(column => (
            <td id={column}>{this.props.data[column]}</td>
          ))
        }
      </tr>
    );
  }
}

export class ArrayTable extends ConsoleComponent {
  render() {
    return (
      <table id="comms_table" export className="tablesorter nicetable noauto">
        <ArrayTableHeader columns={this.props.columns} />
        <tbody>
          {
            this.props.rows.map(row => (
              <ArrayTableRow data={row} columns={this.props.columns} />
            ))
          }
        </tbody>
      </table>
    );
  }
}
