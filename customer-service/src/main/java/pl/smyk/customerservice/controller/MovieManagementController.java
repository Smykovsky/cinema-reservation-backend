package pl.smyk.customerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.customerservice.dto.CustomerDto;
import pl.smyk.customerservice.dto.MovieDto;
import pl.smyk.customerservice.feignClient.AuthServiceClient;
import pl.smyk.customerservice.service.MovieManagementService;

@RestController
@RequestMapping("/api/movie/management")
@RequiredArgsConstructor
public class MovieManagementController {
  private final MovieManagementService movieManagementService;
  private final AuthServiceClient authServiceClient;

  @GetMapping("/add-movie")
  public ResponseEntity<?> addMovie(@RequestHeader("Authorization") String authorizationHeader) {
    CustomerDto customerDto = authServiceClient.getCustomer(authorizationHeader);
    if (customerDto == null) {
      return ResponseEntity.status(404).body("Customer not found");
    }

    if (!customerDto.getRoles().contains("OPERATOR")) {
      return ResponseEntity.status(404).body("Customer is not operator");
    }

    return ResponseEntity.ok(customerDto);
  }
}
