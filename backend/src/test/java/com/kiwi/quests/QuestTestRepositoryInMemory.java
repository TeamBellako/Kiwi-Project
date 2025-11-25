package com.kiwi.quests;

import com.kiwi.features.quests.controllers.QuestRepository;
import com.kiwi.features.quests.data.QuestPersistence;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class QuestTestRepositoryInMemory implements QuestRepository {

    private final InMemoryTestDatabase db;

    public QuestTestRepositoryInMemory(InMemoryTestDatabase db) {
        this.db = db;
    }

    @Override
    public QuestPersistence save(QuestPersistence entity) {
        db.saveQuest(entity);
        return entity;
    }

    @Override
    public Optional<QuestPersistence> findById(Integer id) {
        return Optional.ofNullable(db.quests.get(id));
    }

    @Override
    public List<QuestPersistence> findAll() {
        return new ArrayList<>(db.quests.values());
    }

    // ---- unused methods ----

    @Override public List<QuestPersistence> findAll(Sort sort) { return findAll(); }
    @Override public Page<QuestPersistence> findAll(Pageable pageable) { return Page.empty(); }
    @Override public <S extends QuestPersistence> S saveAndFlush(S entity){return (S) save(entity);}

    @Override
    public <S extends QuestPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<QuestPersistence> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> integers) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public QuestPersistence getOne(Integer integer) {
        return null;
    }

    @Override
    public QuestPersistence getById(Integer integer) {
        return null;
    }

    @Override
    public QuestPersistence getReferenceById(Integer integer) {
        return null;
    }

    @Override public void flush() {}
    @Override public void delete(QuestPersistence entity) {}

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {

    }

    @Override
    public void deleteAll(Iterable<? extends QuestPersistence> entities) {

    }

    @Override public void deleteById(Integer id) {}
    @Override public void deleteAll() {}
    @Override public long count() { return db.quests.size(); }
    @Override public boolean existsById(Integer id) { return db.quests.containsKey(id); }
    @Override public <S extends QuestPersistence> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return (List<S>) findAll();
    }
    @Override public List<QuestPersistence> findAllById(Iterable<Integer> ids) {
        List<QuestPersistence> list = new ArrayList<>();
        ids.forEach(id -> {
            QuestPersistence q = db.quests.get(id);
            if (q != null) list.add(q);
        });
        return list;
    }
    @Override public <S extends QuestPersistence> Optional<S> findOne(Example<S> example) {return Optional.empty();}
    @Override public <S extends QuestPersistence> List<S> findAll(Example<S> example) {return List.of();}
    @Override public <S extends QuestPersistence> List<S> findAll(Example<S> example, Sort sort) {return List.of();}
    @Override public <S extends QuestPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {return Page.empty();}
    @Override public <S extends QuestPersistence> long count(Example<S> example) {return 0;}
    @Override public <S extends QuestPersistence> boolean exists(Example<S> example) {return false;}
    @Override public <S extends QuestPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction){return null;}
}
