import './App.css';
import Enter from "./components/Middle/Main/Enter/Enter";
import WritePost from "./components/Middle/Main/WritePost/WritePost";
import Index from "./components/Middle/Main/Index/Index";
import React, {useCallback, useState} from "react";
import Middle from "./components/Middle/Middle";
import Footer from "./components/Footer/Footer";
import Header from "./components/Header/Header";
import Register from "./components/Middle/Main/Register/Register";
import Users from "./components/Middle/Main/Users/Users";
import Post from "./components/Middle/Main/Post/Post";

function App({usersData, postsData}) {

    const [post, setPost] = useState(null)
    const [user, setUser] = useState(null)
    const [page, setPage] = useState('index')
    const [posts, setPosts] = useState(postsData)
    const [users, setUsers] = useState(usersData)

    const createPost = useCallback((post) => {
        const maxId = Math.max(...posts.map((post) => post.id)) + 1
        setPosts([...posts, {...post, id: maxId}])
    }, [])

    const createUser = useCallback((user) => {
        const maxId = Math.max(...users.map((user) => user.id)) + 1
        setUsers([...users, {...user, id: maxId}])
    }, [])

    const createComment = useCallback((postId, comment) => {
        const post = posts.find(p => p.id === postId);
        const maxId = Math.max(...post.comments.map(c => c.id), 0) + 1;
        const newComment = { ...comment, id: maxId };
        setPosts(posts.map(p =>
            p.id === postId ? { ...p, comments: [...p.comments, newComment] } : p
        ));
        console.log(posts)
    }, [posts]);

    const getPage = useCallback((page) => {
        switch (page) {
            case 'post':
                return (<Post post={post} createComment={createComment} setPost={setPost} setPage={setPage}/>)
            case 'index':
                return (<Index posts={posts} setPost={setPost} setPage={setPage}/>)
            case 'enter':
                return (<Enter users={users} setUser={setUser} setPage={setPage}/>)
            case 'register':
                return (<Register users={users} createUser={createUser} setPage={setPage}/>)
            case 'users':
                return (<Users users={users} setPage={setPage}/>)
            case 'writePost':
                return (<WritePost createPost={createPost} setPage={setPage}/>)
        }
    }, [users, posts, post])

    return (
        <div className="App">
            <Header setUser={setUser} setPage={setPage} user={user}/>
            <Middle
                posts={posts}
                page={getPage(page)}
                setPost={setPost}
                setPage={setPage}
                createComment={createComment}
            />
            <Footer countOfPosts={Object.keys(posts).length} countOfUsers={Object.keys(users).length}/>
        </div>
    );
}

export default App;
