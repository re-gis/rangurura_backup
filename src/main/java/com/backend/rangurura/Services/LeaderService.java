package com.backend.rangurura.services;

import com.backend.rangurura.entities.Leaders;
import com.backend.rangurura.entities.User;
import com.backend.rangurura.repositories.LeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class LeaderService {
    private final LeaderRepository leaderRepository;
    @Autowired

    public LeaderService(LeaderRepository leaderRepository) {
        this.leaderRepository = leaderRepository;
    }


    //this is to get all leaders
    public List<Leaders> getLeaders() {
       System.out.println("All things are okay!");
        return leaderRepository.findAll();
    }

    //this is to  add new leader
    public void addNewLeader(Leaders leaders){
        leaderRepository.save(leaders);

    }
}





