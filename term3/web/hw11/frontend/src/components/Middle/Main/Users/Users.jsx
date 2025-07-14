import React, { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const Users = ({ login, setLogin }) => {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const router = useNavigate();
    const jwtRef = useRef(localStorage.getItem('jwt'));

    const fetchUsers = useCallback(() => {
        const jwt = jwtRef.current;

        if (!jwt) {
            setError('You are not authorized. Redirecting to login...');
            setLogin(null);
            router('/NotFound');
            return;
        }

        axios
            .get('/api/users', {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                },
            })
            .then((response) => {
                setUsers(response.data);
                setError(null);
                setLoading(false);
            })
            .catch((error) => {
                console.error(error);
                setError('Failed to fetch users. Please try again later.');
                setLoading(false);
            });
    }, [router, setLogin]);

    useEffect(() => {
        fetchUsers();
    }, [fetchUsers]);

    useEffect(() => {
        if (!login) {
            router('/NotFound');
        }
    }, [login, router]);

    return (
        <div className="datatable">
            <div className={"caption"}>Users</div>
            {error && <div className="error">{error}</div>}
            {loading ? (
                <div className="info">Loading users...</div>
            ) : users.length > 0 ? (
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Login</th>
                        <th>Creation Time</th>
                    </tr>
                    </thead>
                    <tbody>
                    {users.map((user) => (
                        <tr key={user.id}>
                            <td>{user.id}</td>
                            <td>{user.login}</td>
                            <td>{new Date(user.creationTime).toLocaleString()}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            ) : (
                !error && <div className="info">No users found.</div>
            )}
        </div>
    );
};

export default Users;
