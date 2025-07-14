package ru.itmo.wp.model.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

interface MissingParametersSetter<E>  {
    void set(ResultSet generatedKeys, E instance) throws SQLException;
}
