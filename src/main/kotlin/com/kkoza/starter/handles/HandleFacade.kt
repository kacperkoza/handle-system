package com.kkoza.starter.handles

open class HandleFacade(
        private val handleRepository: HandleRepository,
        private val handleOperation: HandleOperation) {

    fun findByUserId(userId: String): List<HandleDto> = handleRepository.findByUserId(userId)

    fun findById(handleId: String): HandleDocument?  {
        return handleRepository.findById(handleId)
    }

    fun deleteById(handleId: String) = handleRepository.deleteById(handleId)

    fun insert(handleDocument: HandleDocument): HandleDocument = handleOperation.insert(handleDocument)

    fun save(handleDocument: HandleDocument) = handleOperation.save(handleDocument)

}