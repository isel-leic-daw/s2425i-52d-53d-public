import * as React from "react";
import { createRoot } from "react-dom/client";
import {
  createBrowserRouter,
  RouterProvider,
  Link,
} from "react-router-dom";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Home></Home>,
  },
  {
    path: "about",
    element: <About></About>,
  },
]);

function Home() {
    return (
        <div>
          <h1>Hello World</h1>
          <Link to="about">About Us</Link>
          <hr></hr>
          <a href="about">WRONG link with anchor to About Us</a>
        </div>
      )
}
function About() {
    return (<div>About</div>)
}
export function demo01() {
    createRoot(document.getElementById("container")).render(
        <RouterProvider router={router} />
      );      
}