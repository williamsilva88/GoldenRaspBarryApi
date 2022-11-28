package com.api.goldenraspbarry.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwardsBreakDTO {

    private List<AwardedDTO> min;
    private List<AwardedDTO> max;

}
