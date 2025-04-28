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

@RestController
@RequestMapping("/preferences")
public class PreferenceController {
    private final PreferenceService preferenceService;
    public PreferenceController(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PreferenceDTO>> getPreferencesByUserId(@PathVariable(name = "userId") Long userId) throws ResourceNotFoundException {
        List<Preference> preferences = preferenceService.findByUserId(userId);

        List<PreferenceDTO> responseDTOs = preferenceService.toDTOList(preferences);

        if (responseDTOs.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreferenceDTO> getPreferenceById(@PathVariable(name = "id") Long id) {
        Optional<Preference> preferenceOpt = preferenceService.findById(id);
        if (preferenceOpt.isPresent()) {
            Preference preference = preferenceOpt.get();
            PreferenceDTO preferenceDTO = preferenceService.toDTO(preference);
            return ResponseEntity.ok(preferenceDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PreferenceDTO> createPreference(@Valid @RequestBody PreferenceDTO preferenceDTO) throws ResourceNotFoundException, DuplicateResourceException {
        Preference createdPreference = preferenceService.createPreference(preferenceDTO);
        PreferenceDTO createdPreferenceDTO = preferenceService.toDTO(createdPreference);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPreferenceDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreferenceDTO> updatePreference(@PathVariable(name = "id") Long id, @Valid @RequestBody PreferenceDTO preferenceDTO) throws ResourceNotFoundException {
        Preference updatedPreference = preferenceService.updatePreference(id, preferenceDTO);
        PreferenceDTO updatedPreferenceDTO = preferenceService.toDTO(updatedPreference);

        return ResponseEntity.ok(updatedPreferenceDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreference(@PathVariable(name = "id") Long id) throws ResourceNotFoundException {
        preferenceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
