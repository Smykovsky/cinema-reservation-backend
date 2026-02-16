package pl.smyk.common.dto;

import lombok.Data;
import java.util.List;

@Data
public class BatchMovieRequest {
    private List<Long> ids;
}