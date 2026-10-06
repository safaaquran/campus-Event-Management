package com.campus.campus.web.factory;

import com.campus.campus.web.model.EventType;

public class ClubSocialEventFactory extends BaseEventFactory {
    @Override
    protected EventType type() {
        return EventType.CLUB_SOCIAL_EVENT;
    }
}
