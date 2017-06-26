import React from 'react';
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import { createStore, applyMiddleware } from 'redux'
import { epicMiddleware } from 'redux-observable'

import { ConsoleComponent } from 'app/sai-webconsole'

import commsApp from './reducers'
import CommsInformationController from './comms-info'
import CommsTableController from './comms-table'

let store = createStore(commsApp, applyMiddleware(epicMiddleware))

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
);
