package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DbEventServiceImpl implements EventService {
    private final EventStorage eventStorage;

    @Override
    public List<Event> findEventsByUserId(Long idUser) {
        return eventStorage.findAllById(idUser);
    }

    @Override
    public void createAddLikeEvent(Long idUser, Long idFilm) {
        Event event = createEvent(idUser, idFilm, EventType.LIKE, EventOperation.ADD);
        eventStorage.saveOne(event);
    }

    @Override
    public void createRemoveLikeEvent(Long idUser, Long idFilm) {
        Event event = createEvent(idUser, idFilm, EventType.LIKE, EventOperation.REMOVE);
        eventStorage.saveOne(event);
    }

    @Override
    public void createAddReviewEvent(Long idUser, Long idReview) {
        Event event = createEvent(idUser, idReview, EventType.REVIEW, EventOperation.ADD);
        eventStorage.saveOne(event);
    }

    @Override
    public void createRemoveReviewEvent(Long idUser, Long idReview) {
        Event event = createEvent(idUser, idReview, EventType.REVIEW, EventOperation.REMOVE);
        eventStorage.saveOne(event);
    }

    @Override
    public void createUpdateReviewEvent(Long idUser, Long idReview) {
        Event event = createEvent(idUser, idReview, EventType.REVIEW, EventOperation.UPDATE);
        eventStorage.saveOne(event);
    }

    @Override
    public void createAddFriend(Long idUser, Long idFriend) {
        Event event = createEvent(idUser, idFriend, EventType.FRIEND, EventOperation.ADD);
        eventStorage.saveOne(event);
    }

    @Override
    public void createRemoveFriend(Long idUser, Long idFriend) {
        Event event = createEvent(idUser, idFriend, EventType.FRIEND, EventOperation.REMOVE);
        eventStorage.saveOne(event);
    }

    private Event createEvent(Long idUser, Long idEntity, EventType eventType, EventOperation eventOperation) {
        return new Event.Builder()
                .userId(idUser)
                .entityId(idEntity)
                .timestamp(Instant.now().toEpochMilli())
                .eventType(eventType)
                .eventOperation(eventOperation)
                .build();
    }
}
