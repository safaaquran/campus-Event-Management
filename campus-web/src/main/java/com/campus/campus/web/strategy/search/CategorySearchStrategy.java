package com.campus.campus.web.strategy.search;

import com.campus.campus.web.dao.EventDAO;
import com.campus.campus.web.model.Event;

import java.sql.SQLException;
import java.util.List;

public class CategorySearchStrategy implements EventSearchStrategy {
    private final EventDAO eventDAO;

    public CategorySearchStrategy(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    @Override
    public List<Event> search(String query) throws SQLException {
        return eventDAO.searchByCategory(query);
    }
}
