package edu.lytvyniuk.Preference;

/*
  @author darin
  @project microservices
  @class PreferenceController
  @version 1.0.0
  @since 28.04.2025 - 14.11
*/

import edu.lytvyniuk.DTOs.PreferenceDTO;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map; // Якщо повертаємо Map

@RestController
@RequestMapping("/preferences")
public class PreferenceController {
    private final PreferenceService preferenceService;
    // Маппер потрібен, якщо використовуємо для DTO -> Entity
    private final PreferenceMapper preferenceMapper;

    // Конструктор оновлено
    public PreferenceController(PreferenceService preferenceService, PreferenceMapper preferenceMapper) {
        this.preferenceService = preferenceService;
        this.preferenceMapper = preferenceMapper;
    }

    @GetMapping("/user/{userId}") // Отримуємо за ID користувача
    public ResponseEntity<List<PreferenceDTO>> getPreferencesByUserId(@PathVariable(name = "userId") Long userId) throws ResourceNotFoundException {
        List<Preference> preferences = preferenceService.findByUserId(userId);

        // Використовуємо сервіс для перетворення в DTO з отриманням імен/назв
        List<PreferenceDTO> responseDTOs = preferenceService.toDTOList(preferences);

        if (responseDTOs.isEmpty()) {
            // Повертаємо 404 або 200 з порожнім списком, залежить від вимог
            return ResponseEntity.ok(List.of()); // Повертаємо 200 OK з порожнім списком
        }

        // Якщо потрібна групована відповідь (Map), як у старому маппері, логіку треба реалізувати тут або в сервісі
        // Map<String, Object> response = Map.of("preferences", responseDTOs);
        return ResponseEntity.ok(responseDTOs); // Повертаємо просто список DTO
    }

    @GetMapping("/{id}") // Додано ендпоінт для отримання уподобання за його ID
    public ResponseEntity<PreferenceDTO> getPreferenceById(@PathVariable(name = "id") Long id) {
        Optional<Preference> preferenceOpt = preferenceService.findById(id);
        if (preferenceOpt.isPresent()) {
            Preference preference = preferenceOpt.get();
            // Використовуємо сервіс для перетворення в DTO
            PreferenceDTO preferenceDTO = preferenceService.toDTO(preference);
            return ResponseEntity.ok(preferenceDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Повертаємо 201 Created
    public ResponseEntity<PreferenceDTO> createPreference(@Valid @RequestBody PreferenceDTO preferenceDTO) throws ResourceNotFoundException, DuplicateResourceException {
        // Сервіс приймає DTO, отримує з нього username і genreName, знаходить ID і зберігає.
        Preference createdPreference = preferenceService.createPreference(preferenceDTO);

        // Використовуємо сервіс для перетворення збереженої Entity назад в DTO для відповіді
        PreferenceDTO createdPreferenceDTO = preferenceService.toDTO(createdPreference);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPreferenceDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreferenceDTO> updatePreference(@PathVariable(name = "id") Long id, @Valid @RequestBody PreferenceDTO preferenceDTO) throws ResourceNotFoundException {
        // Сервіс оновлює уподобання за ID, використовуючи дані з DTO
        // Припустимо, що updatePreference в сервісі працює з ID і DTO.
        Preference updatedPreference = preferenceService.updatePreference(id, preferenceDTO);

        // Використовуємо сервіс для перетворення оновленої Entity в DTO для відповіді
        PreferenceDTO updatedPreferenceDTO = preferenceService.toDTO(updatedPreference);

        return ResponseEntity.ok(updatedPreferenceDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreference(@PathVariable(name = "id") Long id) throws ResourceNotFoundException {
        preferenceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
