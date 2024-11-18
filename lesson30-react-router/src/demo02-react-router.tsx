import * as React from 'react'
import { createRoot } from 'react-dom/client'
import {
    createBrowserRouter, Link, Outlet, RouterProvider, useParams,
} from 'react-router-dom'

const router = createBrowserRouter([
    {
        "path": "/",
        "element": <Home />,
        "children": [
            {
                "path": "/authors",
                "element": <Authors />,
            },
            {
                "path": "/users/:uid",
                "element": <UserDetail />,
                "children": [
                    {
                        "path": "latest",
                        "element": <UserLatestGames />,
                    },
                    {
                        "path": "stats",
                        "element": <UserStats />,
                    },
                    {
                        "path": "games/:gameid",
                        "element": <UserGameDetail />
                    }
                ]
            }
        ]

    },
    {
        "path": "/path1",
        "element": <Screen1 />
    },
    {
        "path": "/path2",
        "element": <Screen2 />
    },
    {
        "path": "/path3",
        "element": <Screen3 />
    },
])

export function demo02() {
    createRoot(document.getElementById("container")).render(
        <RouterProvider router={router} future={{ v7_startTransition: true }} />
    )
}


function Home() {
    return (
        <div>
            <h1>Home</h1>
            <ol>
                <li><Link to="/me">Me</Link></li>
                <li><Link to="/authors">Authors</Link></li>
                <li><a href="/path1">screen 1 (using a)</a></li>
                <li><Link to="/path1">screen 1</Link></li>
                <li><Link to="/path2">screen 2</Link></li>
                <li><Link to="/path3">screen 3</Link></li>
                <li><Link to="/users/123">User 123</Link></li>
                <li><Link to="/users/abc">User abc</Link></li>
                <li><Link to="/users/123/games/xyz">User 123 Game xyz</Link></li>
            </ol>
            <h3>Child routes</h3>
            <Outlet />
        </div>
    )
}

function Authors() {
    return (
        <div>
            Authors:
            <ul>
                <li>Alice</li>
                <li>Bob</li>
            </ul>
        </div>
    )
}

function Screen1() {
    return (
        <div>
            <h1>Screen 1</h1>
        </div>
    )
}

function Screen2() {
    return (
        <div>
            <h1>Screen 2</h1>
        </div>
    )
}

function Screen3() {
    return (
        <div>
            <h1>Screen 3</h1>
        </div>
    )
}

function UserDetail() {
    const { uid } = useParams()
    return (
        <div>
            <h2>User Detail</h2>
            {uid}
            <p><Link to="latest">Latest Games</Link></p>
            <p><Link to="stats">Statistics</Link></p>
            <Outlet />
        </div>
    )
}

function UserLatestGames() {
    return (
        <div>
            <h4>Latest Games</h4>
            <ol>
                <li>...</li>
                <li>...</li>
            </ol>
        </div>
    )
}

function UserStats() {
    return (
        <div>
            <h4>Statistics</h4>
            <p>Wins: ... </p>
            <p>Draws: ... </p>
            <p>Losses: ... </p>
        </div>
    )
}


function UserGameDetail() {
    const { gid, uid } = useParams()
    return (
        <div>
            <h2>User Game Detail</h2>
            {uid}, {gid}
        </div>
    )
}
