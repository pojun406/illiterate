package com.illiterate.illiterate.ocr.Controller;

import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.Repository.MemberRepository;
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


@RestController
@RequiredArgsConstructor
@RequestMapping("/ocr")
public class OcrController {
    private final OcrRepository ocrRepository;

    private final OcrService ocrService;
    private final MemberRepository memberRepository;

    /*@PostMapping(value = "/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BfResponse<OcrResponseDto>> uploadWantImg(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("file") MultipartFile image) {
        Member user = userDetails.getUser();

        OcrResponseDto responseDto = ocrService.uploadOCRImage(user, image);
        return ResponseEntity.ok(new BfResponse<>(responseDto));
    }*/

    /*@PostMapping(value = "/saveText")
    public ResponseEntity<BfResponse<OcrResponseDto>> saveText(
            @RequestParam Long ocrId,
            @RequestParam String text) {
        OcrResponseDto responseDto = ocrService.saveOcrText(ocrId, text);
        return ResponseEntity.ok(new BfResponse<>(responseDto));
    }


    @PostMapping("/posts")
    public ResponseEntity<BfResponse<List<OcrResponseDto>>> getPosts() {
        List<OcrResponseDto> posts = ocrService.getPosts();
        BfResponse<List<OcrResponseDto>> response = new BfResponse<>(posts);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/posts/{userid}")
    public ResponseEntity<BfResponse<OcrResponseDto>> getPost(@PathVariable Long userid) {
        OcrResponseDto post = ocrService.getPost(userid);
        BfResponse<OcrResponseDto> response = new BfResponse<>(post);
        return ResponseEntity.ok(response);
    }

    // 게시글 삭제
    @PostMapping("/del_post/{ocrid}")
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