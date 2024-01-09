package com.bezkoder.spring.jpa.h2;

import com.bezkoder.spring.jpa.h2.model.Tutorial;
import com.bezkoder.spring.jpa.h2.repository.TutorialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TutorialControllerRestIntegrationTest {

    private static final String BASE_URL = "http://localhost:8080/api/tutorials";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TutorialRepository tutorialRepository;

    @BeforeEach
    public void setUp() {
        // Clear the database before each test
        tutorialRepository.deleteAll();

        // Create and save a couple of tutorials
        Tutorial publishedTutorial = new Tutorial("Published Tutorial", "Description", true);
        Tutorial javaTutorial = new Tutorial("Java", "Java Description", false);
        tutorialRepository.save(publishedTutorial);
        tutorialRepository.save(javaTutorial);
    }

    @Test
    public void testCreateTutorial() {
        Tutorial tutorial = new Tutorial("REST API Integration Test", "Description for Integration Test", false);
        ResponseEntity<Tutorial> response = restTemplate.postForEntity(BASE_URL, tutorial, Tutorial.class);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetTutorialById() {
        Tutorial tutorial = new Tutorial("Get ID Test", "Description for Get ID Test", true);
        tutorial = tutorialRepository.save(tutorial);

        ResponseEntity<Tutorial> response = restTemplate.getForEntity(BASE_URL + "/" + tutorial.getId(), Tutorial.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(tutorial.getId(), response.getBody().getId());
    }

    @Test
    public void testGetAllTutorials() {
        ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL, List.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testUpdateTutorial() {
        Tutorial tutorial = new Tutorial("Update Test", "Before Update", false);
        tutorial = tutorialRepository.save(tutorial);

        tutorial.setTitle("Update Test - After Update");
        restTemplate.put(BASE_URL + "/" + tutorial.getId(), tutorial);

        Tutorial updatedTutorial = tutorialRepository.findById(tutorial.getId()).orElse(null);

        assertNotNull(updatedTutorial);
        assertEquals("Update Test - After Update", updatedTutorial.getTitle());
    }

    @Test
    public void testDeleteTutorial() {
        Tutorial tutorial = new Tutorial("Delete Test", "Description for Delete Test", false);
        tutorial = tutorialRepository.save(tutorial);

        restTemplate.delete(BASE_URL + "/" + tutorial.getId());

        assertFalse(tutorialRepository.existsById(tutorial.getId()));
    }

    @Test
    public void testFindByPublished() {
        ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL + "/published", List.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testFindByTitleContaining() {
        String title = "Java";
        ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL + "?title=" + title, List.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
}
