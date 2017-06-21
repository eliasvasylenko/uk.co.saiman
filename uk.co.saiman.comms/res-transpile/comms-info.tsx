import * as React from 'react'
import { connect } from 'react-redux'
import { ConsoleComponent, MapTable, StatLine } from 'app/sai-web-console'

const CommsInformation = ({name, connection, bundle}) => {
  var entries = {
    name,
    status: connection.status,
    channel: connection.channel,
    bundle: (
      <a href="/system/console/bundles/{bundle.id}">
        {bundle.name}
        <span className="symName">{bundle.symbolicName}</span>
      </a>
    ),
    actions: (
      <span>
        <button>start_polling</button>
        <button>stop_polling</button>
        <button>open_connection</button>
        <button>close_connection</button>
      </span>
    )
  };
  return (
    <div id="comms_information_container">
      <StatLine status="status" />
      <MapTable header="info_header" entries={entries} />
    </div>
  );
}

const mapStateToProps = state => {
  return {
    connection: state.connection,
    bundle: state.bundle
  }
}

const mapDispatchToProps = dispatch => {
  return {
    
  }
}

const CommsInformationController = connect(
  mapStateToProps,
  mapDispatchToProps
)(CommsInformation)

export default CommsInformationController
