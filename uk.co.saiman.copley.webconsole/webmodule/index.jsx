import * as React from 'react';
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import { createStore, applyMiddleware } from 'redux'
import thunk from 'redux-thunk'

import { ConsoleComponent } from '@saiman/webconsole'

import { requestInfo } from './actions.js'
import copleyApp from './reducers.js'
import CopleyInformationController from './controllers.js'
import CopleyTableController from './variables.js'

const store = createStore(copleyApp, applyMiddleware(thunk))
store.dispatch(requestInfo())

class CopleyApp extends ConsoleComponent {
  render() {
    return (
      <div id="copleyApp">
        <CopleyInformationController />
      </div>
    )
  }
}

render(
  <Provider store={store}>
    <CopleyApp />
  </Provider>,
  document.getElementById( 'content' )
)
