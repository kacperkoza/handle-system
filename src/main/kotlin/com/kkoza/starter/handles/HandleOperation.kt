package com.kkoza.starter.handles


class HandleOperation(private val handleRepository: HandleRepository) {

    fun insert(handleDocument: HandleDocument): HandleDocument {
        validateHandleName(handleDocument)
        return handleRepository.insert(handleDocument)
    }

    fun save(handleDocument: HandleDocument) {
        validateHandleName(handleDocument)
        handleRepository.save(handleDocument)
    }

    private fun validateHandleName(handleDocument: HandleDocument) {
        if (handleDocument.name.isBlank()) {
            throw EmptyHandleNameException()
        }
    }

}

class ExistingHandleException(id: String) : RuntimeException("Handle with $id already exists")

class EmptyHandleNameException : RuntimeException("Handle name can't be blank")
