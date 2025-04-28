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


import java.util.List;
import java.util.Optional;

@Service
public class PreferenceService {
    private final PreferenceRepository preferenceRepository;
    private final RestTemplate restTemplate; // Для викликів до інших сервісів

    @Value("http://localhost:9991") // URL user-microservice
    private String userServiceUrl;

    @Value("http://localhost:9992") // URL movie-microservice
    private String movieServiceUrl;

    public PreferenceService(PreferenceRepository preferenceRepository, RestTemplate restTemplate /*, PreferenceMapper preferenceMapper */) {
        this.preferenceRepository = preferenceRepository;
        this.restTemplate = restTemplate;
        // this.preferenceMapper = preferenceMapper;
    }

    // Метод для отримання User ID за username з user-microservice
    private Long getUserIdByUsername(String username) throws ResourceNotFoundException {
        String url = userServiceUrl + "/users/username/" + username;
        try {
            // Припускаємо, що user-service повертає UserDTO (з common-dto)
            ResponseEntity<UserDTO> response = restTemplate.getForEntity(url, UserDTO.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getUserId() != null) {
                return response.getBody().getUserId();
            } else {
                // Це може статися, якщо користувача не знайдено, але сервіс повернув 2xx
                throw new ResourceNotFoundException("User ID not found in response for username: " + username);
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        } catch (Exception e) {
            throw new RuntimeException("Error calling user service: " + e.getMessage(), e);
        }
    }

    // Метод для отримання Genre ID за genreName з movie-microservice
    private Long getGenreIdByGenreName(String genreName) throws ResourceNotFoundException {
        String url = movieServiceUrl + "/genres/name/" + genreName; // Припустимо такий ендпоінт в movie-service
        try {
            // Припускаємо, що movie-service повертає GenreDTO (з common-dto)
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

    // Метод для отримання уподобань за ID користувача
    public List<Preference> findByUserId(Long userId) throws ResourceNotFoundException {
        // Можливо, варто перевірити існування користувача, викликавши user-service
        // try {
        //      restTemplate.getForEntity(userServiceUrl + "/users/" + userId, UserDTO.class);
        // } catch (HttpClientErrorException.NotFound e) {
        //      throw new ResourceNotFoundException("User with id " + userId + " not found");
        // } catch (Exception e) {
        //      throw new RuntimeException("Error checking user existence: " + e.getMessage(), e);
        // }

        List<Preference> preferences = preferenceRepository.findByUserId(userId);
        if (preferences.isEmpty()) {
            // Ви можете кинути виключення, якщо уподобань немає, або повернути порожній список
            // Залежить від вимог API. Повернемо порожній список, як це часто робиться.
            // throw new ResourceNotFoundException("No preferences found for user id: " + userId);
            return List.of();
        }
        return preferences;
    }

    // Метод для отримання одного уподобання за ID
    public Optional<Preference> findById(Long id) {
        return preferenceRepository.findById(id);
    }


    @Transactional
    public Preference createPreference(PreferenceDTO preferenceDTO) throws DuplicateResourceException, ResourceNotFoundException {
        // 1. Отримуємо ID користувача та жанру, викликаючи інші сервіси
        Long userId = getUserIdByUsername(preferenceDTO.getUsername());
        Long genreId = getGenreIdByGenreName(preferenceDTO.getGenreName());

        // 2. Перевіряємо, чи існує вже уподобання для цієї пари користувач-жанр
        if (preferenceRepository.existsByUserIdAndGenreId(userId, genreId)) {
            throw new DuplicateResourceException("Preferences for user '" + preferenceDTO.getUsername() + "' and genre '" + preferenceDTO.getGenreName() + "' already exist.");
        }

        // 3. Створюємо сутність Preference
        Preference preference = new Preference();
        preference.setUserId(userId);
        preference.setGenreId(genreId);
        preference.setPreferredMaxDuration(preferenceDTO.getPreferredMaxDuration());
        preference.setPreferredMinYear(preferenceDTO.getPreferredMinYear());
        preference.setPreferredMaxYear(preferenceDTO.getPreferredMaxYear());
        preference.setPreferredMaxRating(preferenceDTO.getPreferredMaxRating());

        // 4. Зберігаємо уподобання
        return preferenceRepository.save(preference);
    }


    @Transactional
    public Preference updatePreference(Long id, PreferenceDTO preferenceDTO) throws ResourceNotFoundException {
        // 1. Знаходимо існуюче уподобання за ID
        Optional<Preference> existingPreferenceOpt = preferenceRepository.findById(id);
        if (existingPreferenceOpt.isEmpty()) {
            throw new ResourceNotFoundException("Preference with id " + id + " not found");
        }
        Preference existingPreference = existingPreferenceOpt.get();

        // 2. Отримуємо ID користувача та жанру з DTO (може бути інший користувач або жанр,
        // хоча оновлення уподобань зазвичай не змінює користувача і жанр)
        // Якщо дозволяємо змінювати користувача/жанр при оновленні,
        // потрібно перевірити на дублікати для нової пари user_id/genre_id.
        // Припустимо, що користувача і жанр НЕ МОЖНА змінити при оновленні за ID уподобання.
        // Тоді використовуємо userId та genreId з existingPreference.
        // Якщо DTO містить username/genreName, їх можна використовувати для валідації або ігнорувати.
        // Або, якщо DTO містить ID, використовувати їх.

        // Для цього прикладу, припустимо, що DTO містить *нові* значення параметрів уподобань,
        // а користувач і жанр залишаються тими ж, що й у preference з вказаним ID.

        // Якщо ви дозволяєте зміну жанру при оновленні, вам потрібно отримати новий genreId
        // та перевірити унікальність нової пари (userId, новий genreId).
        // Long newGenreId = getGenreIdByGenreName(preferenceDTO.getGenreName());
        // if (!existingPreference.getGenreId().equals(newGenreId)) {
        //     if (preferenceRepository.existsByUserIdAndGenreId(existingPreference.getUserId(), newGenreId)) {
        //          throw new DuplicateResourceException("User already has preferences for genre '" + preferenceDTO.getGenreName() + "'");
        //     }
        //     existingPreference.setGenreId(newGenreId);
        // }


        // Оновлюємо інші поля
        existingPreference.setPreferredMaxDuration(preferenceDTO.getPreferredMaxDuration());
        existingPreference.setPreferredMinYear(preferenceDTO.getPreferredMinYear());
        existingPreference.setPreferredMaxYear(preferenceDTO.getPreferredMaxYear());
        existingPreference.setPreferredMaxRating(preferenceDTO.getPreferredMaxRating());

        // 3. Зберігаємо оновлене уподобання
        return preferenceRepository.save(existingPreference);
    }

    @Transactional
    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!preferenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Preference with id " + id + " not found");
        }
        preferenceRepository.deleteById(id);
    }

    // Допоміжний метод для створення DTO з Entity, отримуючи username та genreName
    public PreferenceDTO toDTO(Preference preference) {
        // Отримуємо username з user-service за preference.getUserId()
        String username;
        try {
            // Припускаємо, user-service має ендпоінт /users/{userId} і повертає UserDTO
            ResponseEntity<UserDTO> userResponse = restTemplate.getForEntity(userServiceUrl + "/users/" + preference.getUserId(), UserDTO.class);
            username = userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null ? userResponse.getBody().getUsername() : "Unknown User";
        } catch (Exception e) {
            // Обробка помилок, якщо user-service недоступний або користувача не знайдено
            System.err.println("Error fetching username for user id " + preference.getUserId() + ": " + e.getMessage());
            username = "Error Fetching User";
        }

        // Отримуємо genreName з movie-service за preference.getGenreId()
        String genreName;
        try {
            // Припускаємо, movie-service має ендпоінт /genres/{genreId} і повертає GenreDTO
            ResponseEntity<GenreDTO> genreResponse = restTemplate.getForEntity(movieServiceUrl + "/genres/" + preference.getGenreId(), GenreDTO.class);
            genreName = genreResponse.getStatusCode().is2xxSuccessful() && genreResponse.getBody() != null ? genreResponse.getBody().getName() : "Unknown Genre";
        } catch (Exception e) {
            // Обробка помилок
            System.err.println("Error fetching genre name for genre id " + preference.getGenreId() + ": " + e.getMessage());
            genreName = "Error Fetching Genre";
        }

        // Створюємо та повертаємо PreferenceDTO
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

    // Допоміжний метод для маппінгу списку Entity в список DTO
    public List<PreferenceDTO> toDTOList(List<Preference> preferences) {
        return preferences.stream()
                .map(this::toDTO) // Використовуємо допоміжний метод для кожного елемента
                .toList();
    }
}
