package pl.smyk.customerservice.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieDateRangeDto {
  private LocalDate start;
  private LocalDate end;
}
