package org.example.vroom.DTOs.requests.ride;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveReviewRequestDTO {
    Integer driverRating;
    Integer vehicleRating;
    String comment;
}
