import * as React from 'react'
import * as ReactDom from 'react-dom/client'

const root = ReactDom.createRoot(document.getElementById('container'))

function FetchAndShow({ uri }: { uri: string}) {
    const [respBody, setRespBody] = React.useState("")
    const [isComplete, setIsComplete ] = React.useState(false)
    React.useEffect(() => {
        let isCancelled = false
        console.log('Fetching ' + uri)
        fetch(uri)
            .then(resp => resp.text())
            .then(body => {
                if(!isCancelled) {
                    console.log('DONE Fetching ' + uri)
                    setRespBody(body)
                    setIsComplete(true)
                }
            })
        return () => { isCancelled = true }
    }, [uri]) // The effect runs again only on uri change
    return (
        <div>
            <p> { isComplete?  "DONE " : "Fetching "}
                {uri}
            </p>
            <textarea cols={40} rows={20} value={respBody} readOnly></textarea>
        </div>
    )
}

export function demo08() {
    root.render(
        /* Unmount of a component */
        <FetchAndShow uri="https://httpbin.org/delay/5"></FetchAndShow>
    )
    setTimeout(() => {
        root.render(
            /* Mount of a component */
            <FetchAndShow uri="https://api.chucknorris.io/jokes/random"></FetchAndShow>
        )
    }, 2000)
}