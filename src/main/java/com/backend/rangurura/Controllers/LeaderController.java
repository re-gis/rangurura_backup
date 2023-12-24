package com.backend.rangurura.Controllers;

import com.backend.rangurura.Services.LeaderService;
import com.backend.rangurura.entities.Leaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/ap/v1/leaders")
public class LeaderController {
    private final LeaderService leaderService;

    @Autowired

    public LeaderController(LeaderService leaderService) {
        this.leaderService = leaderService;
    }
    @GetMapping("/leaders")
    public List<Leaders> getLeaders(){
        return leaderService.getLeaders();
    }

    @PostMapping("/addLeader")
    public void registerLeader(@RequestBody Leaders leaders){
        leaderService.addNewLeader(leaders);
    }
}
