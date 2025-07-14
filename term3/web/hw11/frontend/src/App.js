import './App.css';
import React, {useEffect, useState} from "react";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Application from "./Application";
import Users from "./components/Middle/Main/Users/Users";
import Enter from "./components/Middle/Main/Enter/Enter";
import Index from "./components/Middle/Main/Index/Index";
import Posts from "./components/Middle/Main/Posts/Posts";
import PostPage from "./components/Middle/Main/Posts/PostPage/PostPage";
import NotFound from "./components/Middle/Main/Errors/NotFound/NotFound";
import axios from "axios";
import Register from "./components/Middle/Main/Register/Register";
import WritePost from './components/Middle/Main/WritePost/WritePost';

function App() {
    const [login, setLogin] = useState(null);
    const [posts, setPosts] = useState([]);
    const [users, setUsers] = useState([]);

    useEffect(() => {
        if (localStorage.getItem("jwt")) {
            axios
                .get("/api/jwt", {
                    params: {
                        jwt: localStorage.getItem("jwt"),
                    },
                })
                .then((response) => {
                    localStorage.setItem("login", response.data.login);
                    setLogin(response.data.login);
                })
                .catch((error) => {
                    console.log(error);
                });
        }

        axios
            .get("/api/posts")
            .then((response) => {
                setPosts(response.data);
            })
            .catch((error) => {
                console.log("Error fetching posts:", error);
            });

        axios
            .get("/api/users")
            .then((response) => {
                setUsers(response.data);
            })
            .catch((error) => {
                console.log("Error fetching users:", error);
            });
    }, []);

    return (
        <div className="App">
            <BrowserRouter>
                <Routes>
                    <Route
                        index={true}
                        element={<Application setLogin={setLogin} login={login} page={<Index/>}/>}
                    />
                    <Route
                        path="/enter"
                        element={<Application login={login} page={<Enter setLogin={setLogin}/>}/>}
                    />

                    <Route
                        path="/register"
                        element={<Application setLogin={setLogin} login={login} page={<Register setLogin={setLogin} />} />}
                    />
                    <Route
                        path="/write"
                        element={<Application login={login} page={<WritePost setPage={() => {}} setPosts={setPosts} />} />}
                    />
                    <Route
                        path="/users"
                        element={<Application login={login} users={users} setLogin={setLogin}
                                              page={<Users login={login} setLogin={setLogin}/>}/>}
                    />
                    <Route
                        path="/posts"
                        element={
                            <Application
                                login={login}
                                page={<Posts posts={posts} users={users}/>}
                            />
                        }
                    />
                    <Route
                        path="/posts/:id"
                        element={
                            <Application
                                login={login}
                                page={<PostPage posts={posts} users={users}/>}
                            />
                        }
                    />
                    <Route
                        path="*"
                        element={<Application login={login} page={<NotFound/>}/>}
                    />
                </Routes>
            </BrowserRouter>
        </div>
    );
}
export default App;
