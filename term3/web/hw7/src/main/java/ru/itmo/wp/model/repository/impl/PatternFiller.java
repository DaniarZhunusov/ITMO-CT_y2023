package ru.itmo.wp.model.repository.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PatternFiller {
    void fill(PreparedStatement statement) throws SQLException;
}
