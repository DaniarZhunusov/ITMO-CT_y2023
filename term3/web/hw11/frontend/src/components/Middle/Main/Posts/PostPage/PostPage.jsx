import React from 'react';
import {useParams} from 'react-router-dom';
import Post from '../Post/Post';
import styles from './PostPage.module.css';

const PostPage = ({posts, users}) => {
    const {id} = useParams();
    const post = posts.find((p) => p.id === parseInt(id));

    const getUserLogin = (userId) => {
        const user = users.find((u) => u.id === userId);
        return user ? user.login : 'Unknown';
    };

    if (!post) {
        return <div>Post not found</div>;
    }

    return (
        <div className={styles.postPage}>
            <Post
                post={post}
                getUserLogin={getUserLogin}
                isClickable={false}
                commentCount={0}
            />
        </div>
    );
};

export default PostPage;
