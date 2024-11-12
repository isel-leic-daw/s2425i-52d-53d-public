import * as React from 'react'
import * as ReactDom from 'react-dom/client'

const root = ReactDom.createRoot(document.getElementById('container'))

function FetchAndShow({ uri }: { uri: string}) {
    const [respBody, setRespBody] = React.useState("")
    const [isComplete, setIsComplete ] = React.useState(false)
    fetch(uri)
        .then(resp => resp.text())
        .then(body => {
            setRespBody(body)
            setIsComplete(true)
        })
    return (
        <div>
            <p> { isComplete?  "DONE " : "Fetching "}
                {uri}
            </p>
            <textarea cols={40} rows={20} value={respBody}></textarea>
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