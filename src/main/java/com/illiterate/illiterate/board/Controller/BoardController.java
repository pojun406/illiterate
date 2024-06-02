package com.illiterate.illiterate.board.Controller;



import com.illiterate.illiterate.board.Service.BoardService;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

}