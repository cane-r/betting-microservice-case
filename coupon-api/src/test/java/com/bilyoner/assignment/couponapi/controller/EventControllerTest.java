package com.bilyoner.assignment.couponapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bilyoner.assignment.couponapi.model.EventDTO;
import com.bilyoner.assignment.couponapi.model.enums.EventTypeEnum;
import com.bilyoner.assignment.couponapi.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(controllers = EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldGetAllEvents() throws Exception {
        EventDTO dto = EventDTO.builder().id(1L).name("A-B").type(EventTypeEnum.FOOTBALL).mbs(3).build();
        when(eventService.getAllEvents()).thenReturn(new ArrayList<EventDTO>(){{add(dto);}});

        mockMvc.perform(get("/events")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dto.getId()));
    }
    @Test
    public void shouldCreateEvent() throws Exception {
        EventDTO dto = EventDTO.builder().id(1L).name("A-B").type(EventTypeEnum.FOOTBALL).mbs(3).eventDate(LocalDateTime.now()).build();
        when(eventService.createEvent(any(EventDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()));
    }
    @Test
    @DisplayName("Request should fail if any of the property of EventDTO is not present.")
    public void shouldNotCreateEvent() throws Exception {
        EventDTO dto = EventDTO.builder().id(1L).type(EventTypeEnum.FOOTBALL).mbs(3).eventDate(LocalDateTime.now()).build();
        when(eventService.createEvent(any(EventDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("must not be null"));
    }
}
