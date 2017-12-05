package com.kkoza.starter.handles

import org.springframework.stereotype.Component

@Component
class HandleFacade(
        private val handleRepository: HandleRepository
) {
    fun findByUserId(userId: String): List<HandleDto> {
        return handleRepository.findByUserId(userId)
    }

    fun findById(handleId: String): HandleDto {
        return handleRepository.findById(handleId)
    }

    fun deleteById(handleId: String) {
        handleRepository.deleteById(handleId)
    }

}