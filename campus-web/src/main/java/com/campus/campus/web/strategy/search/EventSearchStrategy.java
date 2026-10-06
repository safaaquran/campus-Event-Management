package com.campus.campus.web.strategy.search;

import com.campus.campus.web.model.Event;

import java.sql.SQLException;
import java.util.List;

public interface EventSearchStrategy {
    List<Event> search(String query) throws SQLException;
}
