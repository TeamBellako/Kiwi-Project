package com.kiwi.quests;

import com.kiwi.features.quests.controllers.UserQuestStatusRepository;
import com.kiwi.features.quests.data.*;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class UserQuestStatusTestRepositoryInMemory implements UserQuestStatusRepository {

    private final InMemoryTestDatabase db;

    public UserQuestStatusTestRepositoryInMemory(InMemoryTestDatabase db) {
        this.db = db;
    }

    @Override
    public UserQuestStatusPersistence save(UserQuestStatusPersistence entity) {
        db.saveUserQuest(entity);
        return entity;
    }

    @Override
    public List<UserQuestStatusPersistence> findByIdUserId(int userId) {
        return db.userQuests.values().stream()
                .filter(s -> s.getId().getUserId() == userId)
                .toList();
    }

    @Override
    public Optional<UserQuestStatusPersistence> findByIdUserIdAndIdQuestId(int userId, int questId) {
        return Optional.ofNullable(db.userQuests.get(new UserQuestStatusKey(userId, questId)));
    }

    @Override public Optional<UserQuestStatusPersistence> findById(UserQuestStatusKey id){return Optional.ofNullable(db.userQuests.get(id));}
    @Override public long count(){return db.userQuests.size();}

    // unused
    @Override public <S extends UserQuestStatusPersistence> S saveAndFlush(S entity){return (S) save(entity);}

    @Override
    public <S extends UserQuestStatusPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<UserQuestStatusPersistence> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UserQuestStatusKey> userQuestStatusKeys) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public UserQuestStatusPersistence getOne(UserQuestStatusKey userQuestStatusKey) {
        return null;
    }

    @Override
    public UserQuestStatusPersistence getById(UserQuestStatusKey userQuestStatusKey) {
        return null;
    }

    @Override
    public UserQuestStatusPersistence getReferenceById(UserQuestStatusKey userQuestStatusKey) {
        return null;
    }

    @Override public void flush(){}
    @Override public void delete(UserQuestStatusPersistence entity){}

    @Override
    public void deleteAllById(Iterable<? extends UserQuestStatusKey> userQuestStatusKeys) {

    }

    @Override
    public void deleteAll(Iterable<? extends UserQuestStatusPersistence> entities) {

    }

    @Override public void deleteAll(){}
    @Override public boolean existsById(UserQuestStatusKey id){return db.userQuests.containsKey(id);}
    @Override public void deleteById(UserQuestStatusKey id){}
    @Override public <S extends UserQuestStatusPersistence> List<S> saveAll(Iterable<S> entities){entities.forEach(this::save);return (List<S>) findAll();}
    @Override public List<UserQuestStatusPersistence> findAll(){return new ArrayList<>(db.userQuests.values());}

    @Override
    public List<UserQuestStatusPersistence> findAllById(Iterable<UserQuestStatusKey> userQuestStatusKeys) {
        return List.of();
    }

    @Override public <S extends UserQuestStatusPersistence> Optional<S> findOne(Example<S> example){return Optional.empty();}
    @Override public <S extends UserQuestStatusPersistence> List<S> findAll(Example<S> example){return List.of();}
    @Override public Page<UserQuestStatusPersistence> findAll(Pageable pageable){return Page.empty();}
    @Override public List<UserQuestStatusPersistence> findAll(Sort sort){return findAll();}
    @Override public <S extends UserQuestStatusPersistence> boolean exists(Example<S> example){return false;}
    @Override public <S extends UserQuestStatusPersistence> Page<S> findAll(Example<S> example, Pageable pageable){return Page.empty();}
    @Override public <S extends UserQuestStatusPersistence> long count(Example<S> example){return 0;}
    @Override public <S extends UserQuestStatusPersistence> List<S> findAll(Example<S> example, Sort sort){return List.of();}
    @Override public <S extends UserQuestStatusPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction){return null;}
}
