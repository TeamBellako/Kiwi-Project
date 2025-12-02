package com.kiwi.quests;

import com.kiwi.features.quests.controllers.SubquestRepository;
import com.kiwi.features.quests.data.SubquestPersistence;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class SubquestTestRepositoryInMemory implements SubquestRepository {

    private final InMemoryTestDatabase db;

    public SubquestTestRepositoryInMemory(InMemoryTestDatabase db) {
        this.db = db;
    }

    @Override
    public SubquestPersistence save(SubquestPersistence entity) {
        db.saveSubquest(entity);
        return entity;
    }

    @Override
    public Optional<SubquestPersistence> findById(Integer id) {
        return Optional.ofNullable(db.subquests.get(id));
    }

    @Override
    public List<SubquestPersistence> findAllByQuestIdOrderByOrderIndex(int questId) {
        return db.subquests.values().stream()
                .filter(s -> s.getQuest().getId() == questId)
                .sorted(Comparator.comparingInt(SubquestPersistence::getOrderIndex))
                .toList();
    }

    @Override
    public List<SubquestPersistence> findAll() {
        return new ArrayList<>(db.subquests.values());
    }

    // ---- unused methods ----
    @Override public List<SubquestPersistence> findAll(Sort sort) {return findAll();}
    @Override public Page<SubquestPersistence> findAll(Pageable pageable) {return Page.empty();}
    @Override public void delete(SubquestPersistence entity) {}

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {

    }

    @Override
    public void deleteAll(Iterable<? extends SubquestPersistence> entities) {

    }

    @Override public void deleteById(Integer id) {}
    @Override public void deleteAll() {}
    @Override public long count() {return db.subquests.size();}
    @Override public boolean existsById(Integer id) { return db.subquests.containsKey(id); }
    @Override public <S extends SubquestPersistence> S saveAndFlush(S entity){return (S) save(entity);}

    @Override
    public <S extends SubquestPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<SubquestPersistence> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> integers) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public SubquestPersistence getOne(Integer integer) {
        return null;
    }

    @Override
    public SubquestPersistence getById(Integer integer) {
        return null;
    }

    @Override
    public SubquestPersistence getReferenceById(Integer integer) {
        return null;
    }

    @Override public void flush() {}
    @Override public <S extends SubquestPersistence> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return (List<S>) findAll();
    }
    @Override public List<SubquestPersistence> findAllById(Iterable<Integer> ids) {return List.of();}
    @Override public <S extends SubquestPersistence> Optional<S> findOne(Example<S> example) {return Optional.empty();}
    @Override public <S extends SubquestPersistence> List<S> findAll(Example<S> example) {return List.of();}
    @Override public <S extends SubquestPersistence> List<S> findAll(Example<S> example, Sort sort) {return List.of();}
    @Override public <S extends SubquestPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {return Page.empty();}
    @Override public <S extends SubquestPersistence> long count(Example<S> example) {return 0;}
    @Override public <S extends SubquestPersistence> boolean exists(Example<S> example) {return false;}
    @Override public <S extends SubquestPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {return null;}
}
