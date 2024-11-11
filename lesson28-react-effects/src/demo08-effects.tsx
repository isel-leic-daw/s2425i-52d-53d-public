import * as React from 'react'
import * as ReactDom from 'react-dom/client'

const root = ReactDom.createRoot(document.getElementById('container'))

function FetchAndShow({uri}: {uri: string}) {
    const [respBody, setRespBody] = React.useState("")
    React.useEffect(() => {
        let cancelled = false
        console.log("Fetching uri: " + uri)
        fetch(uri)
            .then(resp => resp.text())
            .then(body =>  {
                if(!cancelled) {
                    console.log("Fetching FINISH!")
                    setRespBody(body) // Runs after the finish of render
                }
            })
            return () => { cancelled = true}
        }, [ uri ] 
    )
    return (
        <div>
            Fetching from {uri}
            <hr></hr>
            <textarea cols={40} rows={20} value={respBody} readOnly></textarea>
        </div>
    )
}

export function demo08() {
    // Render the Response after 5 seconds
    root.render(
        <FetchAndShow uri="https://httpbin.org/delay/5"></FetchAndShow>
    )
    setTimeout(() => {
        // // Render a new Fetch after 3 seconds
        root.render(
            <FetchAndShow uri="https://api.chucknorris.io/jokes/random"></FetchAndShow>
        )
    }, 3000)
}