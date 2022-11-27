package com.api.goldenraspbarry.repositories;

import com.api.goldenraspbarry.domain.Indicated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndicatedRepository extends JpaRepository<Indicated, Integer> {

    @Query("SELECT idc from Indicated idc where winner = true order by idc.year asc")
    List<Indicated> findIndicatedWinner();

}
