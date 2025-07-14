import React from 'react';
import { useNavigate } from "react-router-dom";

const Navigation = ({ login }) => {

    const router = useNavigate();

    return (
        <nav>
            <ul>
                <li>
                    <a href="" onClick={(event) => {
                        event.preventDefault();
                        router("/posts");
                    }}>Home</a>
                </li>
                <li>
                    <a href="" onClick={(event) => {
                        event.preventDefault();
                        router("/users");
                    }}>Users</a>
                </li>
                {login ? (
                    <li>
                        <a href="" onClick={(event) => {
                            event.preventDefault();
                            router("/write");
                        }}>
                            Write Post
                        </a>
                    </li>
                ) : null}
                <li>
                    <a href="" onClick={(event) => {
                        event.preventDefault();
                        router("/posts");
                    }}>Posts</a>
                </li>
            </ul>
        </nav>
    );
};

export default Navigation;
