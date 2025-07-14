import React, {useCallback, useRef, useState} from 'react';

const Register = ({users, createUser, setUser, setPage}) => {

    const loginInputRef = useRef(null)
    const nameInputRef = useRef(null)
    const [error, setError] = useState('')


    const handleSubmit = (event) => {
        event.preventDefault()
        const login = loginInputRef.current.value
        const name = nameInputRef.current.value

        if (login.trim().length === 0 || name.length === 0) {
            setError('Password or login could not be empty')
            return
        }
        if (!login.match(/^[a-z]+$/)) {
            setError('Login can contain only latin characters')
            return
        }
        if (login.length < 3 || login.length > 16) {
            setError("Login length must be between 3 and 16")
            return
        }
        if (name.length > 32) {
            setError("Login length must be between 1 and 32")
            return
        }

        const loggedIn = users.find((user) => user.login === login)
        if (loggedIn) {
            setError("Login is already in use")
            return
        }

        createUser({
            login: login,
            name: name
        })

        setPage('enter')
    }

    return (
        <div className="registration form-box">
            <div className="header">Register</div>
            <div className="body">
                <form method="post" action="" onSubmit={handleSubmit}>
                    <input type="hidden" name="action" value="register"/>
                    <div className="field">
                        <div className="name">
                            <label htmlFor="login">Login</label>
                        </div>
                        <div className="value">
                            <input
                                autoFocus
                                id="login"
                                name="login"
                                ref={loginInputRef}
                                onChange={() => setError(null)}
                            />
                        </div>
                    </div>
                    <div className="field">
                        <div className="name">
                            <label htmlFor="name">Name</label>
                        </div>
                        <div className="value">
                            <textarea
                                id="name"
                                name="name"
                                ref={nameInputRef}
                                onChange={() => setError(null)}
                            />
                        </div>
                    </div>
                    {error
                        ? <div className={'error'}>{error}</div>
                        : null
                    }
                    <div className="button-field">
                        <input type="submit" value="Register"/>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Register;