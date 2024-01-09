package com.bezkoder.spring.jpa.h2;

import com.bezkoder.spring.jpa.h2.model.Tutorial;
import com.bezkoder.spring.jpa.h2.repository.TutorialRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class TutorialRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TutorialRepository tutorialRepository;

    private Tutorial tutorial;

    @BeforeEach
    public void setUp() {
        tutorial = new Tutorial("Test Tutorial", "Test Description", true);
        entityManager.persist(tutorial);
        entityManager.flush();
    }

    @AfterEach
    public void cleanUp() {
        entityManager.clear();
    }

    @Test
    public void whenFindById_thenReturnTutorial() {
        Optional<Tutorial> found = tutorialRepository.findById(tutorial.getId());
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getTitle()).isEqualTo(tutorial.getTitle());
    }

    @Test
    public void whenFindAll_thenReturnListOfTutorials() {
        List<Tutorial> tutorials = tutorialRepository.findAll();
        assertThat(tutorials).isNotEmpty();
        assertThat(tutorials).hasSize(1);
        assertThat(tutorials.get(0).getTitle()).isEqualTo(tutorial.getTitle());
    }

    @Test
    public void whenFindByPublished_thenReturnPublishedTutorials() {
        List<Tutorial> tutorials = tutorialRepository.findByPublished(true);
        assertThat(tutorials).isNotEmpty();
        assertThat(tutorials.get(0).isPublished()).isTrue();
    }

    @Test
    public void whenFindByTitleContainingIgnoreCase_thenReturnMatchingTutorials() {
        List<Tutorial> tutorials = tutorialRepository.findByTitleContainingIgnoreCase("test");
        assertThat(tutorials).isNotEmpty();
        assertThat(tutorials.get(0).getTitle()).isEqualTo(tutorial.getTitle());
    }

    @Test
    public void whenSave_thenReturnSavedTutorial() {
        Tutorial newTutorial = new Tutorial("New Tutorial", "New Description", false);
        Tutorial saved = tutorialRepository.save(newTutorial);
        assertThat(saved).isNotNull();
        assertThat(saved.getTitle()).isEqualTo(newTutorial.getTitle());
    }

    @Test
    public void whenDelete_thenRemoveTutorial() {
        tutorialRepository.delete(tutorial);
        Optional<Tutorial> deleted = tutorialRepository.findById(tutorial.getId());
        assertThat(deleted.isPresent()).isFalse();
    }
}
