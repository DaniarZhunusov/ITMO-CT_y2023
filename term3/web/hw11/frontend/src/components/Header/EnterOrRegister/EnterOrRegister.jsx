import React from 'react';
import { useNavigate } from 'react-router-dom';

const EnterOrRegister = ({ login, setLogin }) => {
    const navigate = useNavigate();

    return (
        <div className="enter-or-register-box">
            {login ? (
                <>
                    {login}
                    <a
                        href="#"
                        onClick={(event) => {
                            setLogin(null);
                            localStorage.removeItem('jwt');
                            event.preventDefault();
                        }}
                    >
                        Logout
                    </a>
                </>
            ) : (
                <>
                    <a
                        href="#"
                        onClick={(event) => {
                            navigate('/enter');
                            event.preventDefault();
                        }}
                    >
                        Enter
                    </a>
                    <a
                        href="#"
                        onClick={(event) => {
                            navigate('/register');
                            event.preventDefault();
                        }}
                    >
                        Register
                    </a>
                </>
            )}
        </div>
    );
};

export default EnterOrRegister;
