package com.illiterate.illiterate.admin.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illiterate.illiterate.admin.DTO.request.PaperInfoRequestDto;
import com.illiterate.illiterate.admin.DTO.response.AdminResponseDto;
import com.illiterate.illiterate.admin.Service.AdminService;
import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.board.Repository.BoardRepository;
import com.illiterate.illiterate.common.enums.GlobalErrorCode;
import com.illiterate.illiterate.common.enums.GlobalSuccessCode;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.common.response.ErrorResponseHandler;
import com.illiterate.illiterate.common.util.LocalFileUtil;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.Repository.MemberRepository;
import com.illiterate.illiterate.member.exception.BoardException;
import com.illiterate.illiterate.member.exception.MemberException;
import com.illiterate.illiterate.ocr.DTO.response.OcrResponseDto;
import com.illiterate.illiterate.ocr.Entity.PaperInfo;
import com.illiterate.illiterate.security.Service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.illiterate.illiterate.common.enums.BoardErrorCode.NOT_FOUND_WRITING;
import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.SUCCESS;
import static com.illiterate.illiterate.common.enums.MemberErrorCode.NOT_FOUND_MEMBER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final BoardRepository boardRepository;
    private final ErrorResponseHandler errorResponseHandler;
    private final LocalFileUtil localFileUtil;

    @PostMapping("/process/{board_index}")
    public ResponseEntity<BfResponse<?>> processBoardImage(@PathVariable Long board_index, @RequestBody String title){
        try {
            Board board = boardRepository.findByBoardId(board_index)
                    .orElseThrow(() -> new BoardException(NOT_FOUND_WRITING));

            String image_path = board.getRequestImg();

//            PaperInfo paperInfo = adminService.makePaperInfo(image_path, title);

//            return ResponseEntity.ok(new BfResponse<>(SUCCESS, paperInfo));
            return null;
        } catch (Exception e){
            e.printStackTrace();
            return errorResponseHandler.handleErrorResponse(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/paperinfo")
    public ResponseEntity<BfResponse<?>> processPaperInfo(
            @RequestPart("file") MultipartFile file, // 파일 파트
            @RequestPart("infoTitle") String infoTitle // JSON 문자열로 받기
    ) {

        String saved = localFileUtil.saveImage(file, "paperinfo");

        // Flask API 호출
        AdminResponseDto responseDto = adminService.uploadImageAndProcessPaperInfo(saved, infoTitle);

        return ResponseEntity.ok(new BfResponse<>(SUCCESS, responseDto));
    }

}
