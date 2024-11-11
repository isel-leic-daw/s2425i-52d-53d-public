import * as React from 'react'
import * as ReactDom from 'react-dom/client'

const root = ReactDom.createRoot(document.getElementById('container'))

function FetchAndShow({uri}: {uri: string}) {
    const [respBody, setRespBody] = React.useState("")
    /**
     * DON'T DO THIS!!!!
     * The fetch side-effect will run after render has finished,
     * and the setState (i.e. setRespBody) will dispatch a new render,
     * which in turns calls again FetchAndShow function and makes
     * another fetch call, and so on (infinite cycle).
     */
    fetch(uri)
        .then(resp => resp.text())
        .then(body => 
            setRespBody(body) // Runs after rendering is complete
        ) 
    return (
        <div>
            Fetching from {uri}
            <textarea cols={40} rows={20} value={respBody} readOnly></textarea>
        </div>
    )
}

export function demo07() {
    root.render(
        <div>
            <FetchAndShow uri="https://api.chucknorris.io/jokes/random"></FetchAndShow>
        </div>
    )
}