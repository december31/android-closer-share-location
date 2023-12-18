package com.harian.closer.share.location.domain.post.usecase

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.CommentRequest
import com.harian.closer.share.location.data.post.remote.dto.CommentResponse
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(private val postRepository: PostRepository) {
    suspend fun execute(commentRequest: CommentRequest, postId: Int): Flow<BaseResult<CommentEntity, WrappedResponse<CommentResponse>>> {
        return postRepository.createComment(commentRequest, postId)
    }
}
