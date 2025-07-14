import React, {useMemo} from 'react';
import Aside from "./Aside/Aside";

const Middle = ({posts, page, setPost, setPage}) => {

    const aside = useMemo(() => {
        return (<Aside posts={posts} setPost={setPost} setPage={setPage}/>)
    }, [posts])

    return (
        <div className="middle">
            {aside}
            <main>
                {page}
            </main>
        </div>
    );
};


export default Middle;