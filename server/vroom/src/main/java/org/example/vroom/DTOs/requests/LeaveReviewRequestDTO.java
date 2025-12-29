package org.example.vroom.DTOs.requests;

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
