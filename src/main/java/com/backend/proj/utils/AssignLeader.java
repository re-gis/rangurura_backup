package com.backend.proj.utils;

import org.springframework.stereotype.Service;

import com.backend.proj.dtos.RegisterLeaderDto;
import com.backend.proj.entities.Leaders;
import com.backend.proj.entities.User;
import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;
import com.backend.proj.enums.LRole;
import com.backend.proj.enums.URole;
import com.backend.proj.repositories.LeaderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssignLeader {
    private final LeaderRepository leaderRepository;

    public Leaders assigneLeader(Leaders eLeader, RegisterLeaderDto dto, EUrwego urwego) {
        ECategory category = dto.getCategory();
        LRole role = dto.getRole();
        String location = dto.getLocation();
        eLeader.setCategory(category);
        eLeader.setLocation(location);
        eLeader.setRole(role);
        eLeader.setOrganizationLevel(urwego);
        return leaderRepository.save(eLeader);
    }

    public Leaders assignNewLeader(RegisterLeaderDto dto, EUrwego urwego, User user) {
        Leaders leaders = new Leaders();
        ECategory category = dto.getCategory();
        LRole role = dto.getRole();
        String location = dto.getLocation();
        user.setRole(URole.UMUYOBOZI);

        leaders.setCategory(category);
        leaders.setLocation(location);
        leaders.setNationalId(dto.getNationalId());
        leaders.setOrganizationLevel(urwego);
        leaders.setRole(role);

        return leaderRepository.save(leaders);
    }
}
