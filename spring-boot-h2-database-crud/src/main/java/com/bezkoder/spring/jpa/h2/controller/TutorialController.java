package com.bezkoder.spring.jpa.h2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.spring.jpa.h2.model.Tutorial;
import com.bezkoder.spring.jpa.h2.repository.TutorialRepository;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TutorialController {
  private static final Logger logger = LogManager.getLogger(TutorialController.class);

  @Autowired
  TutorialRepository tutorialRepository;

  @GetMapping("/tutorials")
  public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
    logger.debug("Request to fetch all tutorials. Title filter: {}", title);
    try {
      List<Tutorial> tutorials = new ArrayList<>();
      logger.trace("Starting to fetch tutorials from database");

      if (title == null) {
        tutorialRepository.findAll().forEach(tutorials::add);
        logger.trace("Fetched all tutorials");
      } else {
        tutorialRepository.findByTitleContainingIgnoreCase(title).forEach(tutorials::add);
        logger.trace("Fetched tutorials with title containing: {}", title);
      }

      if (tutorials.isEmpty()) {
        logger.warn("No tutorials found with title: {}", title);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      logger.info("Returning all tutorials");
      return new ResponseEntity<>(tutorials, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error fetching tutorials", e);
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/tutorials/{id}")
  public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
    logger.info("Fetching tutorial by ID: {}", id);
    Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

    if (tutorialData.isPresent()) {
      logger.trace("Tutorial found for ID: {}", id);
      return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
    } else {
      logger.warn("Tutorial not found with ID: {}", id);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/tutorials")
  public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
    logger.info("Creating new tutorial with title: {}", tutorial.getTitle());
    try {
      Tutorial _tutorial = tutorialRepository
              .save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), false));
      logger.debug("Tutorial created with ID: {}", _tutorial.getId());
      return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
    } catch (Exception e) {
      logger.error("Error creating tutorial", e);
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/tutorials/{id}")
  public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
    logger.info("Updating tutorial with ID: {}", id);
    Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

    if (tutorialData.isPresent()) {
      Tutorial _tutorial = tutorialData.get();
      _tutorial.setTitle(tutorial.getTitle());
      _tutorial.setDescription(tutorial.getDescription());
      _tutorial.setPublished(tutorial.isPublished());
      logger.debug("Tutorial updated with ID: {}", id);
      return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
    } else {
      logger.warn("Unable to update. Tutorial not found with ID: {}", id);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/tutorials/{id}")
  public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") long id) {
    logger.info("Deleting tutorial with ID: {}", id);
    try {
      tutorialRepository.deleteById(id);
      logger.debug("Tutorial deleted with ID: {}", id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      logger.error("Error deleting tutorial with ID: {}", id, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/tutorials")
  public ResponseEntity<HttpStatus> deleteAllTutorials() {
    logger.info("Deleting all tutorials");
    try {
      tutorialRepository.deleteAll();
      logger.debug("All tutorials deleted");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      logger.error("Error deleting all tutorials", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/tutorials/published")
  public ResponseEntity<List<Tutorial>> findByPublished() {
    logger.info("Fetching all published tutorials");
    try {
      List<Tutorial> tutorials = tutorialRepository.findByPublished(true);

      if (tutorials.isEmpty()) {
        logger.warn("No published tutorials found");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      logger.debug("Returning published tutorials");
      return new ResponseEntity<>(tutorials, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error fetching published tutorials", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}