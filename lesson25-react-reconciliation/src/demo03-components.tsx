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

/*
 * Produces a React table row for the given task
 */
function TaskRowView(props: { task: TaskModel, children: React.ReactNode}) {
    return (
        <tr>
            <td>{props.task.id}</td>
            <td>{props.task.name}</td>
            <td>{props.task.desc}</td>
            <td><input type="text"></input></td>
            {props.children}
        </tr>
    )
}
/*
 * Produces a React table from a list of tasks.
 */
function tasksView(tasks: Array<TaskModel>) {
    return (
        <table>
            <thead>
                <tr><th>Id</th><th>Name</th><th>Desc</th><th>Free Text</th></tr>
            </thead>
            <tbody>
            {
                tasks.map(t => (
                    // ??? How to render the taskRowView
                    <TaskRowView task={t} key={t.id}>
                        <td>Ola ISEL</td>
                    </TaskRowView>
                ))
            }
            </tbody>
        </table>
    )
}

export function demo03() {
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
