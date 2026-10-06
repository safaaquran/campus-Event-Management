package com.campus.campus.web.factory;

import com.campus.campus.web.model.EventType;

public class EventFactoryProvider {
    public EventFactory factoryFor(EventType eventType) {
        return switch (eventType) {
            case WORKSHOP -> new WorkshopFactory();
            case SEMINAR -> new SeminarFactory();
            case CLUB_SOCIAL_EVENT -> new ClubSocialEventFactory();
            case SPORTS_ACTIVITY -> new SportsActivityFactory();
        };
    }
}
