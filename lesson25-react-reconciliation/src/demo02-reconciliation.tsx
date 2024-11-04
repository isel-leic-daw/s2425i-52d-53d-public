import * as React from 'react'
import * as ReactDom from 'react-dom/client'

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
                tasks.map((t) => (
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
    const root = ReactDom
        .createRoot(document.getElementById('container'))

    setInterval(() => {
        const taskId = tasksData.length + 1
        tasksData.unshift({ 
            id: taskId, 
            name: "Task Name" + taskId, 
            desc: "Description for " + taskId},)
        root.render(tasksView(tasksData))
    }, 2000)
}