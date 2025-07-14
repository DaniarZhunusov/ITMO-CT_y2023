import React, { useCallback, useRef, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Register = ({ setLogin }) => {
    const loginInputRef = useRef(null);
    const passwordInputRef = useRef(null);
    const [error, setError] = useState(null);

    const navigate = useNavigate();

    const onRegister = useCallback(() => {
        const login = loginInputRef.current.value;
        const password = passwordInputRef.current.value;

        if (login.trim().length === 0 || password.length === 0) {
            setError('Password or login could not be empty');
            return;
        }

        axios.post('/api/users', {
            login: login,
            password: password,
        })
            .then((response) => {
                const { jwt, login } = response.data;
                localStorage.setItem('jwt', jwt);
                localStorage.setItem('login', login);
                setLogin(login);
                navigate('/');
            })
            .catch((error) => {
                console.error('Error when registering:', error);
                setError(error.response?.data || 'Error. Try again');
            });
    }, [setLogin, navigate]);

    return (
        <div className="register form-box">
            <div className="header">Register</div>
            <div className="body">
                <form
                    onSubmit={(event) => {
                        event.preventDefault();
                        onRegister();
                    }}
                >
                    <input type="hidden" name="action" value="register" />
                    <div className="field">
                        <div className="name">
                            <label htmlFor="login">Login</label>
                        </div>
                        <div className="value">
                            <input
                                autoFocus
                                name="login"
                                ref={loginInputRef}
                                onChange={() => setError(null)}
                            />
                        </div>
                    </div>
                    <div className="field">
                        <div className="name">
                            <label htmlFor="password">Password</label>
                        </div>
                        <div className="value">
                            <input
                                name="password"
                                type="password"
                                ref={passwordInputRef}
                                onChange={() => setError(null)}
                            />
                        </div>
                    </div>
                    {error && <div className="error">{error}</div>}
                    <div className="button-field">
                        <input type="submit" value="Register" />
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Register;
