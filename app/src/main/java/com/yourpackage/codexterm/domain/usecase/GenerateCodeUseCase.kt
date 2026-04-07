package com.yourpackage.codexterm.domain.usecase

import com.yourpackage.codexterm.domain.model.CodeGenerationRequest
import com.yourpackage.codexterm.domain.repository.CodexRepository

class GenerateCodeUseCase(
    private val codexRepository: CodexRepository,
) {
    operator fun invoke(request: CodeGenerationRequest) =
        codexRepository.generateCode(request.prompt, request.language)
}
