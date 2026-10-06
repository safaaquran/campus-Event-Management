package com.campus.campus.web.strategy.search;

import com.campus.campus.web.dao.EventDAO;

public class EventSearchService {
    private final EventDAO eventDAO;

    public EventSearchService(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    public EventSearchStrategy strategyFor(String filter) {
        if (filter == null || filter.isBlank()) {
            return new TitleSearchStrategy(eventDAO);
        }
        return switch (filter) {
            case "TITLE" -> new TitleSearchStrategy(eventDAO);
            case "DEPARTMENT" -> new DepartmentSearchStrategy(eventDAO);
            case "DATE" -> new DateSearchStrategy(eventDAO);
            case "CATEGORY" -> new CategorySearchStrategy(eventDAO);
            case "TYPE" -> new TypeSearchStrategy(eventDAO);
            case "AVAILABILITY" -> new AvailabilitySearchStrategy(eventDAO);
            default -> new TitleSearchStrategy(eventDAO);
        };
    }
}
