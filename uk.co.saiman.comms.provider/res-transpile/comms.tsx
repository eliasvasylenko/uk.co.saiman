import * as React from 'react';
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import { createStore, applyMiddleware } from 'redux'
import thunk from 'redux-thunk'

import { ConsoleComponent } from '@saiman/webconsole'

import { requestInfo } from './actions'
import commsApp from './reducers'
import CommsInformationController from './comms-info'
import CommsTableController from './comms-table'

const store = createStore(commsApp, applyMiddleware(thunk))
store.dispatch(requestInfo())

class CommsApp extends ConsoleComponent {
  render() {
    return (
      <div id="commsApp">
        <CommsInformationController />
        <CommsTableController />
      </div>
    )
  }
}

render(
  <Provider store={store}>
    <CommsApp />
  </Provider>,
  document.getElementById( 'content' )
)
