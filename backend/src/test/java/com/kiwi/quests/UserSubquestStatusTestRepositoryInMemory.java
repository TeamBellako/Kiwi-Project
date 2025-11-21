package com.kiwi.quests;

import com.kiwi.features.quests.controllers.UserSubquestStatusRepository;
import com.kiwi.features.quests.data.*;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class UserSubquestStatusTestRepositoryInMemory implements UserSubquestStatusRepository {

    private final InMemoryTestDatabase db;

    public UserSubquestStatusTestRepositoryInMemory(InMemoryTestDatabase db) {
        this.db = db;
    }

    @Override
    public UserSubquestStatusPersistence save(UserSubquestStatusPersistence entity) {
        db.saveUserSubquest(entity);
        return entity;
    }

    @Override
    public Optional<UserSubquestStatusPersistence> findById(UserSubquestStatusKey key) {
        return Optional.ofNullable(db.userSubquests.get(key));
    }

    @Override
    public Optional<UserSubquestStatusPersistence> findByIdUserIdAndIdSubquestId(int userId, int subquestId) {
        return Optional.ofNullable(db.userSubquests.get(new UserSubquestStatusKey(userId, subquestId)));
    }


    @Override
    public List<UserSubquestStatusPersistence> findByUserIdAndQuestIdOrdered(int userId, int questId) {
        return db.userSubquests.values().stream()
                .filter(s -> s.getId().getUserId() == userId)
                .filter(s -> s.getSubquest().getQuest().getId() == questId)
                .sorted(Comparator.comparingInt(s -> s.getSubquest().getOrderIndex()))
                .toList();
    }

    @Override public long count(){return db.userSubquests.size();}

    // unused
    @Override public void flush(){}
    @Override public <S extends UserSubquestStatusPersistence> S saveAndFlush(S entity){return (S) save(entity);}

    @Override
    public <S extends UserSubquestStatusPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<UserSubquestStatusPersistence> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UserSubquestStatusKey> userSubquestStatusKeys) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public UserSubquestStatusPersistence getOne(UserSubquestStatusKey userSubquestStatusKey) {
        return null;
    }

    @Override
    public UserSubquestStatusPersistence getById(UserSubquestStatusKey userSubquestStatusKey) {
        return null;
    }

    @Override
    public UserSubquestStatusPersistence getReferenceById(UserSubquestStatusKey userSubquestStatusKey) {
        return null;
    }

    @Override public void delete(UserSubquestStatusPersistence entity){}

    @Override
    public void deleteAllById(Iterable<? extends UserSubquestStatusKey> userSubquestStatusKeys) {

    }

    @Override
    public void deleteAll(Iterable<? extends UserSubquestStatusPersistence> entities) {

    }

    @Override public void deleteAll(){}
    @Override public boolean existsById(UserSubquestStatusKey id){return db.userSubquests.containsKey(id);}
    @Override public void deleteById(UserSubquestStatusKey id){}
    @Override public <S extends UserSubquestStatusPersistence> List<S> saveAll(Iterable<S> entities) { entities.forEach(this::save); return (List<S>) findAll(); }
    @Override public List<UserSubquestStatusPersistence> findAll() { return new ArrayList<>(db.userSubquests.values()); }

    @Override
    public List<UserSubquestStatusPersistence> findAllById(Iterable<UserSubquestStatusKey> userSubquestStatusKeys) {
        return List.of();
    }

    @Override public List<UserSubquestStatusPersistence> findAll(Sort sort){return findAll();}
    @Override public Page<UserSubquestStatusPersistence> findAll(Pageable pageable){return Page.empty();}
    @Override public <S extends UserSubquestStatusPersistence> Optional<S> findOne(Example<S> example){return Optional.empty();}
    @Override public <S extends UserSubquestStatusPersistence> List<S> findAll(Example<S> example){return List.of();}
    @Override public <S extends UserSubquestStatusPersistence> List<S> findAll(Example<S> example, Sort sort){return List.of();}
    @Override public <S extends UserSubquestStatusPersistence> Page<S> findAll(Example<S> example, Pageable pageable){return Page.empty();}
    @Override public <S extends UserSubquestStatusPersistence> long count(Example<S> example){return 0;}
    @Override public <S extends UserSubquestStatusPersistence> boolean exists(Example<S> example){return false;}
    @Override public <S extends UserSubquestStatusPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction){return null;}
}
