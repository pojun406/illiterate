package com.illiterate.illiterate.ocr.Controller;

import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.common.enums.MemberErrorCode;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.Repository.MemberRepository;
import com.illiterate.illiterate.member.exception.MemberException;
import com.illiterate.illiterate.ocr.DTO.request.OcrRequestDto;
import com.illiterate.illiterate.ocr.DTO.response.OcrResponseDto;
import com.illiterate.illiterate.ocr.Repository.OcrRepository;
import com.illiterate.illiterate.ocr.Service.OcrService;
import com.illiterate.illiterate.security.Service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.SUCCESS;
import static com.illiterate.illiterate.common.enums.MemberErrorCode.NOT_FOUND_MEMBER_ID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/ocr")
public class OcrController {
    private final OcrRepository ocrRepository;

    private final OcrService ocrService;
    private final MemberRepository memberRepository;

    /**
     * 이미지 업로드 및 OCR 작업 처리
     *
     * @param file 업로드된 이미지 파일
     * @param userDetails 로그인된 사용자 정보 (SecurityContextHolder에서 제공)
     * @return OCR 작업 결과
     */
    @PostMapping("/upload")
    public ResponseEntity<BfResponse<OcrResponseDto>> uploadImageForOcr(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // 로그인된 사용자 정보에서 Member 엔티티 가져오기
        Member member = memberRepository.findByIndex(userDetails.getId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // OCR 처리 및 결과 반환
        OcrResponseDto ocrResult = ocrService.uploadImageAndProcessOcr(file, member);

        System.out.println(ocrResult);

        // OCR 결과를 클라이언트에게 반환
        return ResponseEntity.ok(new BfResponse<>(SUCCESS, ocrResult));
    }

    @PostMapping(value = "/saveText")
    public ResponseEntity<BfResponse<?>> saveText(
            @RequestParam Long ocrId,
            @RequestBody String text) {
        ocrService.saveOcrText(ocrId, text);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS, "SUCCESS"));
    }


    @PostMapping("/posts")
    public ResponseEntity<BfResponse<List<OcrResponseDto>>> getPosts() {
        List<OcrResponseDto> posts = ocrService.getPosts();
        BfResponse<List<OcrResponseDto>> response = new BfResponse<>(posts);
        return ResponseEntity.ok(response);
    }


    /*@PostMapping("/posts/{userid}")
    public ResponseEntity<BfResponse<OcrResponseDto>> getPost(@PathVariable Long userid) {
        OcrResponseDto post = ocrService.getPost(userid);
        BfResponse<OcrResponseDto> response = new BfResponse<>(post);
        return ResponseEntity.ok(response);
    }*/

    // 게시글 삭제
    /*@PostMapping("/del_post/{ocrid}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long ocrid,
            @RequestBody OcrRequestDto requestsDto) {

        if (requestsDto.getUserId() == null) {
            throw new IllegalArgumentException("Member ID must not be null");
        }

        Member user = memberRepository.findById(requestsDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        ocrService.deletePost(ocrid, user.getId());
        return ResponseEntity.noContent().build();
    }*/
}