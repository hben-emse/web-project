package com.bezkoder.spring.jpa.h2.repository;

import com.bezkoder.spring.jpa.h2.model.Tutorial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TutorialRepositoryTest {

    @Mock
    private TutorialRepository tutorialRepository;

    private Tutorial tutorial1;
    private Tutorial tutorial2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tutorial1 = new Tutorial("Java", "Java Description", true);
        tutorial2 = new Tutorial("Python", "Python Description", false);
    }

    @Test
    public void whenFindByPublished_thenReturnTutorials() {
        when(tutorialRepository.findByPublished(true)).thenReturn(Arrays.asList(tutorial1));

        List<Tutorial> foundTutorials = tutorialRepository.findByPublished(true);

        assertFalse(foundTutorials.isEmpty());
        assertTrue(foundTutorials.stream().allMatch(Tutorial::isPublished));
    }

    @Test
    public void whenFindByTitleContainingIgnoreCase_thenReturnTutorials() {
        when(tutorialRepository.findByTitleContainingIgnoreCase("java")).thenReturn(Arrays.asList(tutorial1));

        List<Tutorial> foundTutorials = tutorialRepository.findByTitleContainingIgnoreCase("java");

        assertFalse(foundTutorials.isEmpty());
        assertTrue(foundTutorials.stream().anyMatch(t -> t.getTitle().equalsIgnoreCase("Java")));
    }

    @Test
    public void whenFindById_thenReturnTutorial() {
        when(tutorialRepository.findById(anyLong())).thenReturn(Optional.of(tutorial1));

        Optional<Tutorial> foundTutorial = tutorialRepository.findById(1L);

        assertTrue(foundTutorial.isPresent());
        assertEquals("Java", foundTutorial.get().getTitle());
    }

    @Test
    public void whenSave_thenPersistData() {
        Tutorial newTutorial = new Tutorial("JavaScript", "JavaScript Description", true);
        when(tutorialRepository.save(any(Tutorial.class))).thenReturn(newTutorial);

        Tutorial savedTutorial = tutorialRepository.save(newTutorial);

        assertNotNull(savedTutorial);
        assertEquals("JavaScript", savedTutorial.getTitle());
    }

    @Test
    public void whenDeleteById_thenRemoveData() {
        Long id = 1L;
        doNothing().when(tutorialRepository).deleteById(id);

        tutorialRepository.deleteById(id);

        verify(tutorialRepository, times(1)).deleteById(id);
    }
}
