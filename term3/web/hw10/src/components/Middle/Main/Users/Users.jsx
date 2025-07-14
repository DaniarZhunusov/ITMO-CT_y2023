import React from 'react';


const Users = ({users}) => {
    return (
        <div className="users datatable">
            <div className="caption">Users</div>
            <table>
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Login</th>
                    <th>Name</th>
                </tr>
                </thead>
                <tbody>
                {users.map(user => (
                    <tr key={user.id}>
                        <td >{user.id}</td>
                        <td >{user.login}</td>
                        <td >{user.name}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>

    );
};

export default Users;

