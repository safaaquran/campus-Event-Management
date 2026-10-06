package com.campus.campus.web.strategy.search;

import com.campus.campus.web.dao.EventDAO;
import com.campus.campus.web.model.Event;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class DateSearchStrategy implements EventSearchStrategy {
    private final EventDAO eventDAO;

    public DateSearchStrategy(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    @Override
    public List<Event> search(String query) throws SQLException {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }
        return eventDAO.searchByDate(LocalDate.parse(query));
    }
}
