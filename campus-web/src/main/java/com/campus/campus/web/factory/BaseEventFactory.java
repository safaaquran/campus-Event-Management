package com.campus.campus.web.factory;

import com.campus.campus.web.model.Event;
import com.campus.campus.web.model.EventCreationRequest;
import com.campus.campus.web.model.EventStatus;
import com.campus.campus.web.model.EventType;
import com.campus.campus.web.model.User;

public abstract class BaseEventFactory implements EventFactory {
    protected abstract EventType type();

    @Override
    public Event createEvent(User organizer, EventCreationRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setOrganizerId(organizer.getId());
        String organizerName = request.getOrganizerName();
        if (organizerName == null || organizerName.isBlank()) {
            organizerName = organizer.getFullName();
        }
        event.setOrganizerName(organizerName);
        event.setDescription(request.getDescription());
        event.setDepartmentClub(request.getDepartmentClub());
        event.setEventDateTime(request.getEventDateTime());
        event.setLocation(request.getLocation());
        event.setCapacity(request.getCapacity());
        event.setSeatsRemaining(request.getCapacity());
        event.setCategory(request.getCategory());
        event.setEventType(type());
        event.setEventImage(request.getEventImage());
        event.setStatus(EventStatus.OPEN);
        event.setCompleted(false);
        return event;
    }
}
