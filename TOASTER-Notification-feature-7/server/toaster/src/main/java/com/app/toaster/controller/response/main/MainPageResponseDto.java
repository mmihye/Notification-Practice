package com.app.toaster.controller.response.main;

import java.util.List;

import com.app.toaster.controller.response.category.CategoryResponse;

import lombok.Builder;

@Builder
public record MainPageResponseDto(String nickname, int readToastNum, int allToastNum, List<CategoryResponse> mainCategoryListDto) {
}
