package com.example.FitnessCenterApp.controller.membership;

import com.example.FitnessCenterApp.model.enums.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MembershipDto {
    private String name;
    private double price;
    private String duration;
    private TimeUnit timeUnit;
    private Integer sessionsAvailable;
}
