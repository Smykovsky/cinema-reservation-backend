package pl.smyk.aggregatorservice.dto;

import pl.smyk.common.dto.MovieDto;
import pl.smyk.common.dto.ScreeningDto;

public record ScreeningDetailsDto(
        ScreeningDto screening,
        MovieDto movie
) {}