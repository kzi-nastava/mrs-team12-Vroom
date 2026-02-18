package org.example.vroom.DTOs.requests.ride;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveReviewRequestDTO {

    @NotNull(message = "Driver Rating cant be null")
    @Min(value = 0)
    @Max(value = 5)
    Integer driverRating;

    @NotNull(message = "Vehicle Rating cant be null")
    @Min(value = 0)
    @Max(value = 5)
    Integer vehicleRating;

    @NotBlank(message = "Comment cannot be blank")
    String comment;
}
