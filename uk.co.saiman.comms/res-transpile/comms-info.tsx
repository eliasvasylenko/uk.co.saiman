import * as React from 'react'
import { connect } from 'react-redux'

import { ConsoleComponent, MapTable, StatLine } from 'app/sai-webconsole'

import { openConnection, closeConnection, CONNECTION_STATES } from './actions'

const CommsInformation = ({ name, connection, bundle, setConnectionOpen }) => {
  var entries = {
    name,
    status: (
      <span>
        {connection.status}
        {connection.status != CONNECTION_STATES.OPEN &&
          <button onClick={e => setConnectionOpen(true)}>{i18n.connectionOpen}</button>
        }
        {connection.status != CONNECTION_STATES.CLOSED &&
          <button onClick={e => setConnectionOpen(false)}>{i18n.connectionClose}</button>
        }
      </span>
    ),
    channel: connection.channel,
    bundle: (
      <a href={"/system/console/bundles/" + bundle.id}>
        {bundle.name}
        <span className="symName">{bundle.symbolicName}</span>
      </a>
    )
  }
  return (
    <div id="commsInformationContainer">
      <StatLine status="status" />
      <MapTable header="infoHeader" entries={entries} />
    </div>
  )
}

const mapStateToProps = state => ({
  name: state.name,
  connection: state.connection,
  bundle: state.bundle
})

const mapDispatchToProps = dispatch => ({
  setConnectionOpen: (connectionState) => dispatch(connectionState ? openConnection() : closeConnection())
})

const CommsInformationController = connect(
  mapStateToProps,
  mapDispatchToProps
)(CommsInformation)

export default CommsInformationController
