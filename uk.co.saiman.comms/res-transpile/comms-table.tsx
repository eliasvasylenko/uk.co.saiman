import * as React from 'react'
import { connect } from 'react-redux'

import { ConsoleComponent, FilterBox, TableControls, ArrayTable, StatLine } from 'sai-webconsole'

import { setFilter, clearFilter, setPollingEnabled, sendExecutionRequest } from './actions'

class CommsTable extends ConsoleComponent {
  constructor(props) {
    super(props)
  }

  render() { return this.renderImpl(this.props) }

  renderImpl({ entries, filter, setFilter, clearFilter, pollingEnabled, setPollingEnabled, execute }) {
    return (
      <div id="commsTableContainer">
        <StatLine status="status" />
        <TableControls
          left={
            <FilterBox filter={filter} setFilter={setFilter} clearFilter={clearFilter} />
          }
          right={
            pollingEnabled
              ? <button onClick={e => setPollingEnabled(false)}>{i18n.pollStop}</button>
              : <button onClick={e => setPollingEnabled(true)}>{i18n.pollStart}</button>
          } />
        <ArrayTable
          columns={[
            "entryID",
            "entryOutput",
            "entryInput",
            "entryActions"
          ]}
          rows={
            entries
              .filter(entry => entry.id.toLowerCase().includes(filter.toLowerCase()))
              .map(entry => ({
                entryID: entry.id,
                entryOutput: this.renderItems(entry.output),
                entryInput: this.renderItems(entry.input),
                entryActions: this.renderActions(entry, execute)
              }))
          }
          keyColumn="entryID" />
      </div>
    )
  }

  renderItems(items = {}) {
    return (
      <div>
        {
          Object.keys(items).map(item => this.renderItem(item, items[item]))
        }
      </div>
    )
  }

  renderItem(item, value) {
    return (
      <div key={item}>
        <span>
          <label>{item}</label>
          {
            (typeof value === typeof [])
              ? this.renderValues(value)
              : this.renderValue(value)
          }
        </span>
      </div>
    )
  }

  renderValues(values) {
    return (
      <span>
        {values.map(this.renderValue)}
      </span>
    )
  }

  renderValue(value, index) {
    return (
      <input key={index} value={JSON.stringify(value)} />
    )
  }

  renderActions({ id, actions }, execute) {
    return (
      <div>
        {
          actions.map(action => (
            <button key={action} onClick={e => execute(id, action, {})}>{action}</button>
          ))
        }
      </div>
    )
  }
}

const mapStateToProps = state => ({
  entries: state.entries.map(entry => state.entriesByID[entry]),
  filter: state.entriesFilter,
  pollingEnabled: state.pollingEnabled
})

const mapDispatchToProps = dispatch => ({
  setFilter: (filterText) => dispatch(setFilter(filterText)),
  clearFilter: () => dispatch(clearFilter()),
  setPollingEnabled: (pollingState) => dispatch(setPollingEnabled(pollingState)),
  execute: (entry, action, payload) => dispatch(sendExecutionRequest(entry, action, payload))
})

const CommsTableController = connect(
  mapStateToProps,
  mapDispatchToProps
)(CommsTable)

export default CommsTableController
