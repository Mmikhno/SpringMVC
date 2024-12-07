package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class PostRepositoryImpl implements PostRepository {
    private static Map<Long, Post> posts = new ConcurrentHashMap<>();
    private static long currentId = 0;

    public List<Post> all() {
        var result = posts.entrySet().stream()
                .filter(i -> !i.getValue().isRemoved())
                .map(i -> i.getValue())
                .collect(Collectors.toList());
        return result;
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Post save(Post post) {
        long id = post.getId();
        if (id == 0) {
            post.setId(++currentId);
            posts.put(currentId, post);
        }
        if (id != 0) {
            if (posts.containsKey(id)) {
                if (posts.get(id).isRemoved()) {
                    throw new NotFoundException();
                } else {
                    posts.put(id, post);
                }
            } else {
                post.setId(++currentId);
                posts.put(currentId, post);
            }
        }
        return post;
    }

    public void removeById(long id) {
        if (posts.containsKey(id)) {
            Post post = getById(id).get();
            if (!post.isRemoved()) {
                post.setRemoved(true);
            } else {
                throw new NotFoundException();
            }
        }
    }
}
