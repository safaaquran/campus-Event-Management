package com.campus.campus.web.factory;

import com.campus.campus.web.model.EventType;

public class SportsActivityFactory extends BaseEventFactory {
    @Override
    protected EventType type() {
        return EventType.SPORTS_ACTIVITY;
    }
}
