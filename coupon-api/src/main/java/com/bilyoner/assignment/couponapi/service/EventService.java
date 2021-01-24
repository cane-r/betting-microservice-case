package com.bilyoner.assignment.couponapi.service;

import com.bilyoner.assignment.couponapi.entity.EventEntity;
import com.bilyoner.assignment.couponapi.exception.CouponApiException;
import com.bilyoner.assignment.couponapi.exception.ErrorCodeEnum;
import com.bilyoner.assignment.couponapi.model.EventDTO;
import com.bilyoner.assignment.couponapi.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventDTO> getAllEvents() {
        return getEvents();
    }

    public List<EventDTO> getEvents() {
        return eventRepository.findAll().stream().map(entity -> EventDTO.builder()
                                                                        .name(entity.getName())
                                                                        .mbs(entity.getMbs())
                                                                        .type(entity.getType())
                                                                        .eventDate(entity.getEventDate())
                                                                        .id(entity.getId())
                                                                        .build())
                                                    .collect(Collectors.toList());
    }

    public EventDTO createEvent(@Valid EventDTO eventRequest) {
        final EventEntity createdEventEntity = eventRepository.save(EventEntity.builder()
                .name(eventRequest.getName())
                .mbs(eventRequest.getMbs())
                .type(eventRequest.getType())
                .eventDate(eventRequest.getEventDate())
                .build());

        final EventDTO response = EventDTO.mapToEventDTO(createdEventEntity);

        return response;
    }

    public List<EventEntity> getEventsById(List<Long> eventIds) {
        return eventRepository.findAllById(eventIds);
    }
    public EventEntity getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                    .orElseThrow(() -> new CouponApiException(ErrorCodeEnum.CONTENT_NOT_FOUND_ERROR,String.format("Event with id %d not found!",eventId)));
    }
}
