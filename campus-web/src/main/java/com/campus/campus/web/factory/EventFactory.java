package com.campus.campus.web.factory;

import com.campus.campus.web.model.Event;
import com.campus.campus.web.model.EventCreationRequest;
import com.campus.campus.web.model.User;

public interface EventFactory {
    Event createEvent(User organizer, EventCreationRequest request);
}
