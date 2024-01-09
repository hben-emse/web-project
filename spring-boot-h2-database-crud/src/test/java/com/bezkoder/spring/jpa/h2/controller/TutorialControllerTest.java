package com.bezkoder.spring.jpa.h2.controller;

import com.bezkoder.spring.jpa.h2.model.Tutorial;
import com.bezkoder.spring.jpa.h2.repository.TutorialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TutorialControllerTest {

    @Mock
    private TutorialRepository tutorialRepository;

    @InjectMocks
    private TutorialController tutorialController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tutorialController).build();
    }

    @Test
    void getAllTutorialsTest() throws Exception {
        when(tutorialRepository.findAll()).thenReturn(Arrays.asList(new Tutorial("Title1", "Desc1", false),
                new Tutorial("Title2", "Desc2", true)));

        mockMvc.perform(get("/api/tutorials")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Title1")))
                .andExpect(jsonPath("$[1].description", is("Desc2")));
    }

    @Test
    void getTutorialByIdTest() throws Exception {
        when(tutorialRepository.findById(1L)).thenReturn(Optional.of(new Tutorial("Title1", "Desc1", false)));

        mockMvc.perform(get("/api/tutorials/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Title1")))
                .andExpect(jsonPath("$.published", is(false)));
    }

    @Test
    void createTutorialTest() throws Exception {
        Tutorial newTutorial = new Tutorial("New Title", "New Description", false);
        when(tutorialRepository.save(any(Tutorial.class))).thenReturn(newTutorial);

        mockMvc.perform(post("/api/tutorials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Title\",\"description\":\"New Description\",\"published\":false}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Title")));
    }

    @Test
    void updateTutorialTest() throws Exception {
        Tutorial existingTutorial = new Tutorial("Old Title", "Old Description", false);
        when(tutorialRepository.findById(1L)).thenReturn(Optional.of(existingTutorial));
        when(tutorialRepository.save(any(Tutorial.class))).thenReturn(existingTutorial);

        mockMvc.perform(put("/api/tutorials/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Title\",\"description\":\"Updated Description\",\"published\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")));
    }

    @Test
    void deleteTutorialTest() throws Exception {
        mockMvc.perform(delete("/api/tutorials/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAllTutorialsTest() throws Exception {
        doNothing().when(tutorialRepository).deleteAll();

        mockMvc.perform(delete("/api/tutorials")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByPublishedTest() throws Exception {
        when(tutorialRepository.findByPublished(true)).thenReturn(Arrays.asList(new Tutorial("Published Title", "Published Description", true)));

        mockMvc.perform(get("/api/tutorials/published")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Published Title")));
    }
}
