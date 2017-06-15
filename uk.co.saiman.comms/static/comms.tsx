var refresh = function() {
	initStaticWidgets();
}

var Statline = createReactClass( {
	componentDidMount: refresh,
  render: function() {
    return (
      <p className="statline">React is working!</p>
    )
  }
} );

var CommsInformation = createReactClass( {
	componentDidMount: refresh,
  render: function() {
    return (
      <p className="dunno">Info...</p>
    )
  }
} );

var CommsTableControls = createReactClass( {
  render: function() {
    return (
    	<div id="filter_container" className="ui-widget-header ui-corner-top buttonGroup">
    		<input className="filter" value="" title="{i18n.filter_help}"></input>
    		<span className="filterClear ui-icon ui-icon-close" title="{i18n.filter_clear}"></span>
    		<button className="filterButton" type="button" title="{i18n.filter_help}">{i18n.filter_apply}</button>
    		<button className="pollButton" type="button"></button>
   		</div>
    )
  }
} );

var CommsTableHeader = createReactClass( {
	render: function() {
    return (
			<th className="comms_header" id="{props.name}">{props.name}</th>
    )
  }
} );

var CommsTable = createReactClass( {
	componentDidMount: refresh,
	componentDidUpdate: refresh,
  render: function() {
    return (
    	<div id="comms_table_container">
    		<CommsTableControls />
	    	<table id="comms_table" className="tablesorter nicetable noauto">
	    		<thead>
	    			<tr>
	    				<CommsTableHeader>{i18n.command_id}</CommsTableHeader>
	    				<CommsTableHeader>{i18n.command_output}</CommsTableHeader>
	    				<CommsTableHeader>{i18n.command_input}</CommsTableHeader>
	    				<CommsTableHeader>{i18n.command_actions}</CommsTableHeader>
	    			</tr>
	    		</thead>
	    		<tbody>
	    		</tbody>
	    	</table>
	    </div>
    )
  }
} );

var Comms = createReactClass( {
  render: function() {
    return (
      <div id="comms">
      	<CommsInformation />
      	<Statline />
      	<CommsTable />
      </div>
    )
  }
} );

ReactDOM.render(
  <Comms />,
  document.getElementById( 'content' )
);