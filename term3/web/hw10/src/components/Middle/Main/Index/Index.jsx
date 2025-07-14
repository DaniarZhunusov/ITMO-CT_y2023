import React, {useMemo} from 'react';

import PostItem from "../PostItem/PostItem";


const Index = ({posts, setPost, setPage}) => {
    const sortedPosts = useMemo(() => {
        if (!posts)
            return []
        return posts.sort((a, b) => b.id - a.id)
    }, [posts])

    return (
        <div>
            {sortedPosts.map((post) =>
                <PostItem post={post} setPost={setPost} setPage={setPage}/>
            )}
        </div>
    );
};

export default Index;