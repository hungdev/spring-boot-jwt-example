package com.fortna.hackathon.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fortna.hackathon.dao.RoundDao;
import com.fortna.hackathon.dto.TournamentDto;
import com.fortna.hackathon.dto.TournamentDto.MatchDto;
import com.fortna.hackathon.dto.TournamentDto.WinnerDto;
import com.fortna.hackathon.entity.Match;
import com.fortna.hackathon.entity.Round;
import com.fortna.hackathon.service.TournamentService;
import com.fortna.hackathon.utils.ImageProcessing;

@Service(value = "tournamentService")
@Transactional
public class TournamentServiceImpl implements TournamentService {

    private static final Logger logger = LoggerFactory.getLogger(TournamentServiceImpl.class);

    @Autowired
    private RoundDao roundDao;

    @Override
    public TournamentDto getTournament() {
        List<Round> rounds = roundDao.findAllByOrderByIdAsc();
        if (rounds == null || rounds.isEmpty()) {
            logger.error("No round found!");
            return null;
        }

        TournamentDto tournament = new TournamentDto();
        List<MatchDto> teams = new ArrayList<>();
        List<List<WinnerDto>> results = new ArrayList<>();

        for (Round r : rounds) {
            List<WinnerDto> resultOfRound = new ArrayList<>();
            List<Match> matches = r.getMatches();
            logger.info("Found {} matches for round {}", matches.size(), r.getName());
            for (Match m : matches) {
                MatchDto obj = new MatchDto();
                obj.setId(m.getId());

                if (m.getPlayer0() != null) {
                    obj.setFirstPlayer(m.getPlayer0().getDisplayName());
                    if (m.getPlayer0().getAvatar() != null)
                        obj.setFirstPlayerAvatar(ImageProcessing.compressAvatar(m.getPlayer0().getAvatar()));
                }

                if (m.getPlayer1() != null) {
                    obj.setSecondPlayer(m.getPlayer1().getDisplayName());
                    if (m.getPlayer1().getAvatar() != null)
                        obj.setSecondPlayerAvatar(ImageProcessing.compressAvatar(m.getPlayer1().getAvatar()));
                }
                teams.add(obj);

                if (m.isResultPublished() && m.getFinalWinner() != null) {
                    WinnerDto winnerDto = new WinnerDto();
                    winnerDto.setWinnerId(m.getFinalWinner().getId());
                    winnerDto.setWinnerName(m.getFinalWinner().getDisplayName());
                    resultOfRound.add(winnerDto);
                    logger.info("Match {} between {} and {}. Final winner is {}", obj.getId(), obj.getFirstPlayer(),
                            obj.getSecondPlayer(), m.getFinalWinner().getDisplayName());
                } else {
                    WinnerDto winnerDto = new WinnerDto();
                    winnerDto.setWinnerId(null);
                    winnerDto.setWinnerName(null);
                    resultOfRound.add(winnerDto);
                    logger.info("Match {} between {} and {}. Final winner is not published yet or the game is draw!",
                            obj.getId(), obj.getFirstPlayer(), obj.getSecondPlayer());
                }
            }
            results.add(resultOfRound);
        }

        tournament.setTeams(teams);
        tournament.setResults(results);
        return tournament;
    }

}
