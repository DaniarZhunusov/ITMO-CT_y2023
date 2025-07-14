
const Post = ({ post, getUserLogin, commentCount = 0, showCommentsLink = false, onCommentsClick = null, isClickable = true }) => {
    return (
        <article>
            <div className="title">
                {isClickable ? (
                    <a
                        href="#"
                        onClick={(e) => {
                            e.preventDefault();
                            if (onCommentsClick) onCommentsClick(post);
                        }}
                    >
                        {post.title}
                    </a>
                ) : (
                    <span>{post.title}</span>
                )}
            </div>
            <div className="information">
                By {getUserLogin(post.user.id)},
            </div>
            <div className="body">{post.text}</div>
            <div className="footer">
                <div className="left">
                    <img
                        src="/img/voteup.png"
                        title="Vote Up"
                        alt="Vote Up"
                    />
                    <span className="positive-score">+{post.upvotes || 123}</span>
                    <img
                        src="/img/votedown.png"
                        title="Vote Down"
                        alt="Vote Down"
                    />
                </div>
                <div className="right">
                    <img
                        src="/img/comments_16x16.png"
                        title="Comments"
                        alt="Comments"
                    />
                    {showCommentsLink ? (
                        <a
                            href="#"
                            onClick={(e) => {
                                e.preventDefault();
                                if (onCommentsClick) onCommentsClick(post);
                            }}
                        >
                            {commentCount} comments
                        </a>
                    ) : (
                        <span>{commentCount} </span>
                    )}
                </div>
            </div>
        </article>
    );
};

export default Post;
