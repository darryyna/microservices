package edu.lytvyniuk.Preference;

/*
  @author darin
  @project microservices
  @class PreferenceService
  @version 1.0.0
  @since 28.04.2025 - 14.11
*/

import edu.lytvyniuk.DTOs.GenreDTO;
import edu.lytvyniuk.DTOs.PreferenceDTO;
import edu.lytvyniuk.DTOs.UserDTO;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.List;
import java.util.Optional;

@Service
public class PreferenceService {
    private final PreferenceRepository preferenceRepository;
    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${movie.service.url}")
    private String movieServiceUrl;

    public PreferenceService(PreferenceRepository preferenceRepository, RestTemplate restTemplate) {
        this.preferenceRepository = preferenceRepository;
        this.restTemplate = restTemplate;
    }

    private Long getUserIdByUsername(String username) throws ResourceNotFoundException {
        String url = userServiceUrl + "/users/username/" + username;
        try {
            ResponseEntity<UserDTO> response = restTemplate.getForEntity(url, UserDTO.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getUserId() != null) {
                return response.getBody().getUserId();
            } else {
                throw new ResourceNotFoundException("User ID not found in response for username: " + username);
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        } catch (Exception e) {
            throw new RuntimeException("Error calling user service: " + e.getMessage(), e);
        }
    }

    private Long getGenreIdByGenreName(String genreName) throws ResourceNotFoundException {
        String url = movieServiceUrl + "/genres/name/" + genreName;
        try {
            ResponseEntity<GenreDTO> response = restTemplate.getForEntity(url, GenreDTO.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getGenreId() != null) {
                return response.getBody().getGenreId();
            } else {
                throw new ResourceNotFoundException("Genre ID not found in response for genre name: " + genreName);
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Genre not found with name: " + genreName);
        } catch (Exception e) {
            throw new RuntimeException("Error calling movie service: " + e.getMessage(), e);
        }
    }

    public List<Preference> findByUserId(Long userId) throws ResourceNotFoundException {
        List<Preference> preferences = preferenceRepository.findByUserId(userId);
        if (preferences.isEmpty()) {
            return List.of();
        }
        return preferences;
    }

    public Optional<Preference> findById(Long id) {
        return preferenceRepository.findById(id);
    }


    public Preference createPreference(PreferenceDTO preferenceDTO) throws DuplicateResourceException, ResourceNotFoundException {
        Long userId = getUserIdByUsername(preferenceDTO.getUsername());
        Long genreId = getGenreIdByGenreName(preferenceDTO.getGenreName());

        if (preferenceRepository.existsByUserIdAndGenreId(userId, genreId)) {
            throw new DuplicateResourceException("Preferences for user '" + preferenceDTO.getUsername() + "' and genre '" + preferenceDTO.getGenreName() + "' already exist.");
        }
        Preference preference = new Preference();
        preference.setUserId(userId);
        preference.setGenreId(genreId);
        preference.setPreferredMaxDuration(preferenceDTO.getPreferredMaxDuration());
        preference.setPreferredMinYear(preferenceDTO.getPreferredMinYear());
        preference.setPreferredMaxYear(preferenceDTO.getPreferredMaxYear());
        preference.setPreferredMaxRating(preferenceDTO.getPreferredMaxRating());
        return preferenceRepository.save(preference);
    }


    public Preference updatePreference(Long id, PreferenceDTO preferenceDTO) throws ResourceNotFoundException {
        Optional<Preference> existingPreferenceOpt = preferenceRepository.findById(id);
        if (existingPreferenceOpt.isEmpty()) {
            throw new ResourceNotFoundException("Preference with id " + id + " not found");
        }
        Preference existingPreference = existingPreferenceOpt.get();
        existingPreference.setPreferredMaxDuration(preferenceDTO.getPreferredMaxDuration());
        existingPreference.setPreferredMinYear(preferenceDTO.getPreferredMinYear());
        existingPreference.setPreferredMaxYear(preferenceDTO.getPreferredMaxYear());
        existingPreference.setPreferredMaxRating(preferenceDTO.getPreferredMaxRating());

        return preferenceRepository.save(existingPreference);
    }

    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!preferenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Preference with id " + id + " not found");
        }
        preferenceRepository.deleteById(id);
    }

    public PreferenceDTO toDTO(Preference preference) {
        String username;
        try {
            ResponseEntity<UserDTO> userResponse = restTemplate.getForEntity(userServiceUrl + "/users/" + preference.getUserId(), UserDTO.class);
            username = userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null ? userResponse.getBody().getUsername() : "Unknown User";
        } catch (Exception e) {
            System.err.println("Error fetching username for user id " + preference.getUserId() + ": " + e.getMessage());
            username = "Error Fetching User";
        }

        String genreName;
        try {
            ResponseEntity<GenreDTO> genreResponse = restTemplate.getForEntity(
                    movieServiceUrl + "/genres/" + preference.getGenreId(), GenreDTO.class);
            genreName = genreResponse.getStatusCode().is2xxSuccessful() && genreResponse.getBody() != null ?
                    genreResponse.getBody().getName() : "Unknown Genre";
        } catch (Exception e) {
            System.err.println("Error fetching genre name for genre id " + preference.getGenreId() + ": " + e.getMessage());
            e.printStackTrace();
            genreName = "плакі плакі";
        }

        PreferenceDTO dto = new PreferenceDTO();
        dto.setPreferenceId(preference.getPreferenceId());
        dto.setUsername(username);
        dto.setGenreName(genreName);
        dto.setPreferredMaxDuration(preference.getPreferredMaxDuration());
        dto.setPreferredMinYear(preference.getPreferredMinYear());
        dto.setPreferredMaxYear(preference.getPreferredMaxYear());
        dto.setPreferredMaxRating(preference.getPreferredMaxRating());

        return dto;
    }

    public List<PreferenceDTO> toDTOList(List<Preference> preferences) {
        return preferences.stream()
                .map(this::toDTO)
                .toList();
    }
}
