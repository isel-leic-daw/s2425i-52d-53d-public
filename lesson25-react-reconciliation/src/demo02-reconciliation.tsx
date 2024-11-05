import * as React from 'react'
import * as ReactDOM from 'react-dom/client'

type TaskModel = {
    id: number, 
    name: string,
    desc: string
}

const tasksData: Array<TaskModel> = [
    { id: 1, name: "swim", desc: "Workout swimming 1500m in 30 minutes"},
    { id: 2, name: 'Learn React', desc: "Learn React with TypeScript and WebPack"}
]

function tasksView(tasks: Array<TaskModel>) {
    return (
        <table>
            <thead>
                <tr><th>Id</th><th>Name</th><th>Desc</th><th>Free Text</th></tr>
            </thead>
            <tbody>
            {
                tasks.map(t => (
                    <tr key={t.id}>
                        <td>{t.id}</td>
                        <td>{t.name}</td>
                        <td>{t.desc}</td>
                        <td><input type="text"></input></td>
                    </tr>
                ))
            }
            </tbody>
        </table>
    )
}

export function demo02() {
    const root = ReactDOM.createRoot(document.getElementById('container'))
    setInterval(() => {
        // Don't do this => State should be IMMUTABLE => Don't call render
        // ONLY for demo
        const nextId = tasksData.length + 1
        tasksData.unshift({
            id: nextId,
            name: 'Task ' + nextId,
            desc: 'Description of Task ' + nextId
        })
        root.render(tasksView(tasksData))
    }, 1000)
    
}
