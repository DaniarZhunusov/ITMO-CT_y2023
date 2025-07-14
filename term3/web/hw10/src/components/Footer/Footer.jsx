import React from 'react';

const Footer = ({countOfPosts, countOfUsers}) => {
    return (
        <footer>
            <a href="#">Codehorses</a> 2099 by Mike Mirzayanov <br/>
            count of posts: {countOfPosts}, count of users: {countOfUsers}
        </footer>
    );
};

export default Footer;