package edu.lytvyniuk.Preference;

import edu.lytvyniuk.customException.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreferenceServiceTest {

    @Mock
    private PreferenceRepository preferenceRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PreferenceService preferenceService;

    private Preference preference;

    @BeforeEach
    void setUp() {
        preference = new Preference();
        preference.setPreferenceId(1L);
        preference.setUserId(1L);
        preference.setGenreId(1L);
        preference.setPreferredMaxDuration(120);
        preference.setPreferredMinYear(2000);
        preference.setPreferredMaxYear(2023);
        preference.setPreferredMaxRating(10.0);
    }

    @Test
    void findByUserId_whenPreferencesExist_shouldReturnList() throws ResourceNotFoundException {
        when(preferenceRepository.findByUserId(1L)).thenReturn(Collections.singletonList(preference));

        List<Preference> preferences = preferenceService.findByUserId(1L);

        assertFalse(preferences.isEmpty());
        assertEquals(1, preferences.size());
        assertEquals(120, preferences.get(0).getPreferredMaxDuration());
    }

    @Test
    void findByUserId_whenNoPreferencesExist_shouldReturnEmptyList() throws ResourceNotFoundException {
        when(preferenceRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        List<Preference> preferences = preferenceService.findByUserId(1L);

        assertTrue(preferences.isEmpty());
    }

    @Test
    void findById_whenExists_shouldReturnPreference() {
        when(preferenceRepository.findById(1L)).thenReturn(Optional.of(preference));

        Optional<Preference> found = preferenceService.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(120, found.get().getPreferredMaxDuration());
    }

    @Test
    void deleteById_whenExists_shouldDelete() throws ResourceNotFoundException {
        when(preferenceRepository.existsById(1L)).thenReturn(true);

        preferenceService.deleteById(1L);

        verify(preferenceRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_whenDoesNotExist_shouldThrowException() {
        when(preferenceRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> preferenceService.deleteById(1L));
    }
}
