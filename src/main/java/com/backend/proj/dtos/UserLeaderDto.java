package com.backend.proj.dtos;

import com.backend.proj.entities.User;
import com.backend.proj.entities.Leaders;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLeaderDto {
    private User user;
    private Leaders leader;
}

