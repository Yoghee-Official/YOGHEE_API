package com.lagavulin.yoghee.controller;

import com.lagavulin.yoghee.model.dto.PresignedFileDto;
import com.lagavulin.yoghee.model.swagger.main.image.ImagePresignResponseDto;
import com.lagavulin.yoghee.model.swagger.main.image.ImageUploadDto;
import com.lagavulin.yoghee.service.ImageService;
import com.lagavulin.yoghee.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
@Tag(name = "Image", description = "이미지 관련 API")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/presign")
    @Operation(summary = "이미지 presign API", description = "이미지 등록 전 presign URL 발급",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "파일 업로드 Model",
        required = true,
        content = @Content(schema =  @Schema(implementation = ImageUploadDto.class))
    ),
    responses = {
        @ApiResponse(responseCode = "200", description = "Presigned URL 발급 성공",
            content = @Content(schema = @Schema(implementation = ImagePresignResponseDto.class))
            )
    })
    public ResponseEntity<?> presign(@RequestBody PresignedFileDto presignedFileDto){
        return ResponseUtil.success(imageService.generatePresignedUrls(presignedFileDto));
    }
}
