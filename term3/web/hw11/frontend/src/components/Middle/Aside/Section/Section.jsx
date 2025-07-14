import React from 'react';
import { useNavigate } from 'react-router-dom';

const Section = ({ post }) => {
    const navigate = useNavigate();

    const handleViewAll = (e) => {
        e.preventDefault();
        navigate(`/posts/${post.id}`);
    };

    return (
        <section>
            <div className="header">
                {post.title}
            </div>
            <div className="body">
                {post.text}
            </div>
            <div className="footer">
                <a href="" onClick={handleViewAll}>View all</a>
            </div>
        </section>
    );
};

export default Section;
