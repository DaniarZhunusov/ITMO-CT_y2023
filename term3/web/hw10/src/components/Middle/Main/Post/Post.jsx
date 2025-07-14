import React, {useRef, useState} from 'react';

import PostItem from "../PostItem/PostItem";


const Post = ({post, createComment, setPost, setPage}) => {

    const authorInputRef = useRef(null)
    const textInputRef = useRef(null)
    const [error, setError] = useState('')

    const handleSubmit = (event) => {
        event.preventDefault()
        const author = authorInputRef.current.value
        const text = textInputRef.current.value
        if (author.trim().length === 0 || text.trim().length === 0) {
            setError('author or text can not be empty')
            return
        }
        createComment(post.id, {author: author, text: text})
        console.log(post)
        //setPost(post)
        setPost({ ...post, comments: [...post.comments, {author, text}] })
        //authorInputRef.current.value = ''
        textInputRef.current.value = ''
        setPage('post')
    }

    return (
        <div>
            <PostItem post={post} setPost={setPost} setPage={setPage}/>

            <div className="form_comments">
                <form method="post" action="" onSubmit={handleSubmit}>
                    <input type="hidden" name="action" value="writePost"/>
                    <div className="field">
                        <div className="name">
                            <label htmlFor="text">Add comment</label>
                        </div>
                        <div className="name">
                            <label htmlFor="author">Author</label>
                        </div>
                        <div className="value">
                            <input
                                autoFocus
                                id="author"
                                name="author"
                                ref={authorInputRef}
                                onChange={() => setError(null)}
                            />
                        </div>
                        <div className="value">
                            <textarea
                                id="text"
                                name="text"
                                ref={textInputRef}
                                onChange={() => setError(null)}
                            />
                        </div>
                        {error
                            ? <div className={'error'}>{error}</div>
                            : null
                        }
                    </div>
                    <div className="button-field">
                        <input type="submit" value="Add"/>
                    </div>
                </form>
            </div>

            <div className="comments">
                {post.comments.map(comment => (
                    <section>
                        <div className="body">
                            {comment.text}
                        </div>
                        <div className="user">
                            by {comment.author}
                        </div>
                    </section>
                ))}
            </div>
        </div>
    )
        ;
};

export default Post;