package com.backend.rangurura.Controllers;

import com.backend.rangurura.entities.Leaders;
import com.backend.rangurura.services.LeaderService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/ap/v1/leaders")
public class LeaderController {

    private final LeaderService leaderService;

    @GetMapping("/leaders")
    public List<Leaders> getLeaders() {
        return leaderService.getLeaders();
    }

    @PostMapping("/addLeader")
    public void registerLeader(@RequestBody Leaders leaders) {
        leaderService.addNewLeader(leaders);
    }
}
