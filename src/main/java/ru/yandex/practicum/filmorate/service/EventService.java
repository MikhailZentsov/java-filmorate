package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Service
public interface EventService {

    List<Event> findEventsByUserId(Long idUser);

    void createAddLikeEvent(Long idUser, Long idFilm);

    void createRemoveLikeEvent(Long idUser, Long idFilm);

    void createAddReviewEvent(Long idUser, Long idReview);

    void createRemoveReviewEvent(Long idUser, Long idReview);

    void createUpdateReviewEvent(Long idUser, Long idReview);

    void createAddFriend(Long idUser, Long idFriend);

    void createRemoveFriend(Long idUser, Long idFriend);
}
