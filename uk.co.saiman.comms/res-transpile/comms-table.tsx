import * as React from 'react'
import { connect } from 'react-redux'
import { ConsoleComponent, FilterBox, TableControls, ArrayTable, StatLine } from 'app/sai-web-console'

const CommsTable = ({commands, apply_filter, is_polling, toggle_polling}) =>
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

const mapStateToProps = state => {
  return {
    commands: state.commands.map(command => state.commandsById[command]),
    is_polling: false
  }
}

const mapDispatchToProps = dispatch => {
  return {
    apply_filter: dispatch(setFilter(ownState.filter_text)),
    toggle_polling: dispatch(togglePolling())
  }
}

export default CommsTableController = connect(
  mapStateToProps,
  mapDispatchToProps
)(CommsTable)
