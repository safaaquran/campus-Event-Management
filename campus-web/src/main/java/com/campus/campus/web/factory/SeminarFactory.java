package com.campus.campus.web.factory;

import com.campus.campus.web.model.EventType;

public class SeminarFactory extends BaseEventFactory {
    @Override
    protected EventType type() {
        return EventType.SEMINAR;
    }
}
