package com.example.miniproject14.service;

import com.example.miniproject14.dto.ApplicantsRequestDto;
import com.example.miniproject14.dto.GeneralResponseDto;
import com.example.miniproject14.dto.StatusResponseDto;
import com.example.miniproject14.entity.Applicants;
import com.example.miniproject14.entity.Board;
import com.example.miniproject14.entity.User;
import com.example.miniproject14.repository.ApplicantsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicantsService {

    private final ApplicantsRepository applicantsRepository;

    @Transactional
    public GeneralResponseDto addApplicants(Board board, User user) {
        try {
            if (board.getMemberNum() >= board.getTotalMember()) {
                return new StatusResponseDto("참여 인원이 꽉 찼습니다.", HttpStatus.BAD_REQUEST);
            }
            Applicants applicants = new Applicants(board, user);
            applicantsRepository.save(applicants);
            board.setMemberNum(board.getMemberNum() + 1);
            return new StatusResponseDto("신청이 완료되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public GeneralResponseDto deleteApplicants(ApplicantsRequestDto applicantsRequestDto, User user) {
        if(user == null){
            throw new IllegalArgumentException("로그인이 필요합니다");
        }
        Optional<Applicants> existingApplicant = applicantsRepository.findByUserIdAndBoardId(user.getId(), applicantsRequestDto.getBoardId());
        if (existingApplicant.isPresent()) {
            try {
                applicantsRepository.deleteByUserIdAndBoardId(user.getId(), applicantsRequestDto.getBoardId());
                return new StatusResponseDto("신청이 취소되었습니다.", HttpStatus.OK);
            } catch(Exception e){
                return new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new StatusResponseDto("신청하지 않은 게시물입니다.", HttpStatus.BAD_REQUEST);
        }
//        try {
//            applicantsRepository.deleteByUserIdAndBoardId(user.getId(), applicantsRequestDto.getBoardId());
//            return new StatusResponseDto("신청이 취소되었습니다.", HttpStatus.OK); // DB에 정상적으로 저장 되었을 경우 결과 리턴
//        }catch(Exception e){
//            return new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
    }


}
