import { html } from 'lighterhtml'

export const StatLine = ({ status }) => html`<p className="statline">${status}</p>`

export const MapTableEntry = ({ entry, entries }) => html`
	<tr key=${entry}>
		<td id="item">${translate(entry)}</td>
		<td id="value">${entries[entry]}</td>
	</tr>
`

export const MapTable = ({ header, entries, translate }) => html`
	<table id=${header} className="maptable tablesorter nicetable noauto">
		<thead>
			<tr><th colSpan=${2} className="ui-widget-header">${translate(header)}:</th></tr>
		</thead>
		<tbody>
			${
				Object.keys(entries).map(entry => 
					MapTableEntry({ entry: entry, entries: entries })
				)
			}
		</tbody>
	</table>
`

export const ArrayTableHeader = ({ columns, translate }) => html`
	<thead>
		<tr>
			${ columns.map(column => html`<th id=${column} key=${column}>${translate(column)}</th>`) }
		</tr>
	</thead>
`

export const ArrayTableRow = ({ data, columns, id }) => html`
	<tr id=${id} key=${id}>
		${ columns.map(column => html`<td id=${column} key=${column}>${data[column]}</td>`) }
	</tr>
`

export const ArrayTable = ({ rows, columns, keyColumn, translate }) => html`
	<table className="arrayTable tablesorter nicetable noauto">
		${ ArrayTableHeader({columns: columns, translate: translate}) }
		<tbody>
			${rows.map(row => 
				ArrayTableRow({data: row, columns: columns, id: row[keyColumn]})
			)}
		</tbody>
	</table>
`

export const TableControls = ({ left, right }) => html`
	<div className="tableControls ui-widget-header ui-corner-top buttonGroup">
		<span className="tableControlsLeft">${left}</span>
		<span className="tableControlsRight">${right}</span>
	</div>
`

export const FilterBox = ({ filter, setFilter, clearFilter, translate }) => html`
	<span>
		<input className="filter" value=${filter} title=${translate("filter.help")} onChange=${e => setFilter(e.target.value)}></input>
		<span className="filterClear ui-icon ui-icon-close" title=${translate("filter.clear")} onClick=${e => clearFilter()}></span>
	</span>
`
