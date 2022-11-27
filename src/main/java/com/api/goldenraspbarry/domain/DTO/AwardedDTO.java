package com.api.goldenraspbarry.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwardedDTO implements Comparable<AwardedDTO> {
    private String producer;
    private Integer interval;
    private Integer previousWin;
    private Integer followingWin;

    @Override
    public int compareTo(AwardedDTO otherAwardedDTO) {
        if (this.interval > otherAwardedDTO.getInterval()) {
            return -1;
        } if (this.interval < otherAwardedDTO.getInterval()) {
            return 1;
        }
        return 0;
    }
}
