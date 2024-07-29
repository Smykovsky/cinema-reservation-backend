package pl.smyk.customerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.customerservice.dto.CustomerDto;
import pl.smyk.customerservice.dto.MovieDto;
import pl.smyk.customerservice.dto.response.MovieResponse;
import pl.smyk.customerservice.feignClient.AuthServiceClient;
import pl.smyk.customerservice.service.MovieManagementService;

@RestController
@RequestMapping("/api/movie/management")
@RequiredArgsConstructor
public class MovieManagementController {
  private final MovieManagementService movieManagementService;
  private final AuthServiceClient authServiceClient;

  @PostMapping("/add-movie")
  public ResponseEntity<?> addMovie(@RequestHeader("Authorization") String authorizationHeader, @RequestBody MovieDto movieDto) {
    CustomerDto customerDto = authServiceClient.getCustomer(authorizationHeader);
    if (customerDto == null) {
      return ResponseEntity.status(404).body("Customer not found");
    }

    if (!customerDto.getRoles().contains("OPERATOR")) {
      return ResponseEntity.status(404).body("Customer is not operator");
    }

    MovieResponse movieResponse = movieManagementService.addMovie(movieDto);

    return ResponseEntity.status(movieResponse.getStatus()).body(movieResponse.getMessage());
  }

  @PatchMapping("/update-movie")
  public ResponseEntity<?> updateMovie(@RequestHeader("Authorization") String authorizationHeader, @RequestBody MovieDto movieDto) {
    CustomerDto customerDto = authServiceClient.getCustomer(authorizationHeader);
    if (customerDto == null) {
      return ResponseEntity.status(404).body("Customer not found!");
    }

    if (!customerDto.getRoles().contains("OPERATOR")) {
      return ResponseEntity.status(404).body("Customer is not operator");
    }

    MovieResponse movieResponse = movieManagementService.updateMovie(movieDto);
    return ResponseEntity.status(movieResponse.getStatus()).body(movieResponse.getMessage());
  }

  @DeleteMapping("/delete-movie/{id}")
  public ResponseEntity<?> deleteMovie(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id) {
    CustomerDto customerDto = authServiceClient.getCustomer(authorizationHeader);
    if (customerDto == null) {
      return ResponseEntity.status(404).body("Customer not found!");
    }

    if (!customerDto.getRoles().contains("OPERATOR")) {
      return ResponseEntity.status(404).body("Customer is not operator");
    }

    MovieResponse movieResponse = movieManagementService.deleteMovie(id);

    return ResponseEntity.status(movieResponse.getStatus()).body(movieResponse.getMessage());
  }
}
