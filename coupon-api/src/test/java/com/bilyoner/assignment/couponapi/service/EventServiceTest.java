package com.bilyoner.assignment.couponapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bilyoner.assignment.couponapi.entity.EventEntity;
import com.bilyoner.assignment.couponapi.model.EventDTO;
import com.bilyoner.assignment.couponapi.model.enums.EventTypeEnum;
import com.bilyoner.assignment.couponapi.repository.EventRepository;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;

    @Test
    public void shouldGetAllEvents() {

        EventEntity eventEntity = EventEntity.builder().name("A-B").mbs(3).type(EventTypeEnum.BASKETBALL).build();
        
        when(eventRepository.findAll()).thenReturn(new ArrayList<EventEntity>(){{add(eventEntity);}});

        List<EventDTO> res = eventService.getAllEvents();

        verify(eventRepository, times(1)).findAll();

        verifyNoMoreInteractions(eventRepository);

        assertNotNull(res);
    }
    @Test
    public void shouldCreateEvent() {
        EventDTO req = EventDTO.builder().name("A-B").type(EventTypeEnum.FOOTBALL).mbs(3).id(1L).eventDate(LocalDateTime.now()).build();

        EventEntity entity = EventEntity.builder().name(req.getName()).eventDate(req.getEventDate()).mbs(req.getMbs()).eventDate(req.getEventDate()).type(req.getType()).build();

        ArgumentCaptor<EventEntity> argumentCaptor = ArgumentCaptor.forClass(EventEntity.class);

        when(eventRepository.save(any())).thenReturn(entity);

        eventService.createEvent(req);

        verify(eventRepository, times(1)).save(argumentCaptor.capture());

        verifyNoMoreInteractions(eventRepository);

        EventEntity converted = argumentCaptor.getValue();

        assertEquals(converted,entity);
    }
    @Test
    public void shouldGetById() {
        Long id = 1L;

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(EventEntity.builder().build()));

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        eventService.getEventById(id);

        verify(eventRepository, times(1)).findById(argumentCaptor.capture());

        verifyNoMoreInteractions(eventRepository);

        assertEquals(id,argumentCaptor.getValue());
    }
}
