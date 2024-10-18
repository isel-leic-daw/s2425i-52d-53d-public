import http from 'http';

http
    .createServer(serverHandler)
    .listen(4000, () => {
        console.log("Listening on port 4000")
    })

let connectionCounter = 1
function serverHandler(req, resp) {
    switch(req.url) {
        case "/":
            resp.writeHead(200, {'Content-Type': 'text/html'})
            resp.end(`<html>
                Navigate to <a href="/sse/listen">/sse/listen</<a>
                to start receiving events.
                </html>`)
            break
        case "/sse/listen":
            console.log(`Client connected to event stream (
                connection #${connectionCounter}, 
                Last-Event-Id: ${req.headers['last-event-id']}) `)
            emitSse(connectionCounter++, req, resp)
            break
        default:
            resp
                resp.writeHead(404);
                resp.write("Resource not found!!!!")
                resp.end()
            break
    }
}

function emitSse(connId, req, resp) {
    let eventCounter = 1
    // Headers
    resp.writeHead(200, {
        'Content-Type': 'text/event-stream',
        'Cache-Control': 'no-cache' // let intermediaries know to not cache anything
    })
    // Body
    const emitter = setInterval(() => {
        resp.write('event: my-custom-event\n')
        resp.write(`id: ${connId*1000 + eventCounter}\n`)
        resp.write(`data: Server says hi! (event #${eventCounter} of connection #${connId})\n\n`)
        eventCounter++
    }, 1000)

    // On close stop emitter
    req.on('close', () => {
        console.log(`Client disconnected (connection (#${connId}))`)
        resp.end()
        clearInterval(emitter)
    })
}