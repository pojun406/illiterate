package com.illiterate.illiterate.admin.Controller;

import com.illiterate.illiterate.admin.Service.AdminService;
import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.board.Repository.BoardRepository;
import com.illiterate.illiterate.common.enums.GlobalErrorCode;
import com.illiterate.illiterate.common.enums.GlobalSuccessCode;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.common.response.ErrorResponseHandler;
import com.illiterate.illiterate.member.exception.BoardException;
import com.illiterate.illiterate.ocr.Entity.PaperInfo;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.illiterate.illiterate.common.enums.BoardErrorCode.NOT_FOUND_WRITING;
import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final BoardRepository boardRepository;
    private final ErrorResponseHandler errorResponseHandler;

    @PostMapping("/process/{board_index}")
    public ResponseEntity<BfResponse<?>> processBoardImage(@PathVariable Long board_index, @RequestBody String title){
        try {
            Board board = boardRepository.findByBoardId(board_index)
                    .orElseThrow(() -> new BoardException(NOT_FOUND_WRITING));

            String image_path = board.getRequestImg();

            PaperInfo paperInfo = adminService.makePaperInfo(image_path, title);

            return ResponseEntity.ok(new BfResponse<>(SUCCESS, paperInfo));
        } catch (Exception e){
            e.printStackTrace();
            return errorResponseHandler.handleErrorResponse(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
