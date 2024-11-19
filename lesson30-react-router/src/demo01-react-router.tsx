import * as React from "react";
import { createRoot } from "react-dom/client";
import {
    createBrowserRouter,
    RouterProvider,
    Link,
    useParams,
} from "react-router-dom";

const router = createBrowserRouter([
    { path: "/", element: <Home></Home>,},
    { path: "about", element: <About></About>, },
    { path: "tasks", element: <TasksList></TasksList>, },
    { path: "tasks/:id", element: <TaskDetails></TaskDetails>, },
]);

export function demo01(){
    createRoot(document.getElementById("container")).render(
        <RouterProvider router={router} />
    )
}

type Task = {}
const tasks: Array<Task> = []

function TasksList() { 
    return <div></div>
}
function TaskDetails() {
    const { id } = useParams()
    return (<div></div>)
}

function Home() {
    return (
        <div>
            <h1>Hello World</h1>
            <Link to="about">About Us</Link>
            { /* 
               * !!! UNDESIRED behavior => HTTP request
               * <a href="about">About Us</a> 
               */ 
            }
        </div>
    )
}
function About() {
    return <div>About</div>
}