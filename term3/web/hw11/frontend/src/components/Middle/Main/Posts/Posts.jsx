import React from 'react';
import Post from './Post/Post';

const Posts = ({ posts, users }) => {
    const getUserLogin = (userId) => {
        const user = users.find((u) => u.id === userId);
        return user ? user.login : 'Unknown';
    };

    return (
        <div>
            {posts.map((post) => {
                return (
                    <Post
                        key={post.id}
                        post={post}
                        getUserLogin={getUserLogin}
                        commentCount={0}
                        showCommentsLink={false}
                        isClickable={false}
                    />
                );
            })}
        </div>
    );
};

export default Posts;