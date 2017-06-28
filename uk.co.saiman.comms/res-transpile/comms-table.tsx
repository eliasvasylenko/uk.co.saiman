import * as React from 'react'
import { connect } from 'react-redux'

import { ConsoleComponent, FilterBox, TableControls, ArrayTable, StatLine } from 'sai-webconsole'

import { setFilter, clearFilter, setPollingEnabled, sendExecutionRequest } from './actions'

class CommsTable extends ConsoleComponent {
  constructor(props) {
    super(props)
  }

  render() { return this.renderImpl(this.props) }

  renderImpl({ entries, filter, setFilter, pollingEnabled, setPollingEnabled, execute }) {
    return (
      <div id="commsTableContainer">
        <StatLine status="status" />
        <TableControls
          left={
            <FilterBox filter setFilter />
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
            entries.map(entry => ({
              entryID: entry.id,
              entryOutput: this.renderOutput(entry.output),
              entryInput: this.renderInput(entry.input),
              entryActions: this.renderActions(entry, execute)
            }))
          }
          keyColumn="entryID" />
      </div>
    )
  }

  renderOutput(output) {
    return (
      <div>
        {
          Object.keys(output).map(item => (
            <input key={item}>{item}</input>
          ))
        }
      </div>
    )
  }

  renderInput(input) {
    return <div></div>
  }

  renderActions({ id, actions }, execute) {
    return (
      <div>
        {
          Object.keys(actions).map(action => (
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
  setPollingEnabled: (pollingState) => dispatch(setPollingEnabled(pollingState)),
  execute: (entry, action, payload) => dispatch(sendExecutionRequest(entry, action, payload))
})

const CommsTableController = connect(
  mapStateToProps,
  mapDispatchToProps
)(CommsTable)

export default CommsTableController
