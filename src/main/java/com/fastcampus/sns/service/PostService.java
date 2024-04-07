package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.AlarmArgs;
import com.fastcampus.sns.model.AlarmType;
import com.fastcampus.sns.model.Comment;
import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.model.entity.*;
import com.fastcampus.sns.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final AlarmService alarmService;

    @Transactional
    public void create(String title, String body, String userName) {
        // user find
        UserEntity userEntity = getUserOrException(userName);
        // post save
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    @Transactional
    public Post modify(Integer userId, String title, String body, Integer postId) {
        // post exist
        PostEntity postEntity = getPostOrException(postId);

        // post permission
        if (!Objects.equals(postEntity.getUser().getId(), userId)) {
           throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("user %s has no permission with %s", userId, postId));
        }

        // post save
        postEntity.setTitle(title);
        postEntity.setBody(body);
        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(Integer userId, Integer postId) {
        // post exist
        PostEntity postEntity = getPostOrException(postId);
        // post permission
        if (!Objects.equals(postEntity.getUser().getId(), userId)) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("user %s has no permission with post %d", userId, postId));
        }
        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        // post delete
        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(Integer userId, Pageable pageable) {
        return postEntityRepository.findAllByUserId(userId, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        // post exist
        PostEntity postEntity = getPostOrException(postId);
        // user find
        UserEntity userEntity = getUserOrException(userName);

        //  check liked -> throw
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
                throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already like post %d", userName, postId));
        });
        //  likeEntityRepository
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));

        alarmEntityRepository.save(AlarmEntity.of(AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(userEntity.getId(), postId), postEntity.getUser()));
    }

    @Transactional
    public long likeCount(Integer postId) {
        // post exist
        PostEntity postEntity = getPostOrException(postId);

        //  check liked -> throw
        /*List<LikeEntity> likeEntities = likeEntityRepository.findAllByPost(postEntity);
        return likeEntities.size();*/

        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Integer postId, String userName, String comment) {
        // post exist
        PostEntity postEntity = getPostOrException(postId);
        // user find
        UserEntity userEntity = getUserOrException(userName);

        //comment save
        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));

        //
        alarmEntityRepository.save(AlarmEntity.of(AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(), postId), postEntity.getUser()));
    }

    private PostEntity getPostOrException(Integer postId) {
        return postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not found", postId)));
    }

    private UserEntity getUserOrException(String userName) {
        return userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity postEntity = getPostOrException(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }
}
