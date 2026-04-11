package com.kiwi.skills;

import com.kiwi.features.skills.controllers.SkillEffectRepository;
import com.kiwi.features.skills.data.persistence.SkillEffectPersistence;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SkillEffectTestRepositoryInMemory implements SkillEffectRepository {

    private final List<SkillEffectPersistence> store = new ArrayList<>();

    @Override
    public List<SkillEffectPersistence> findBySkillIdIn(List<Long> skillIds) {
        return store.stream()
                .filter(e -> skillIds.contains(e.getSkillId()))
                .toList();
    }

    @Override
    public <S extends SkillEffectPersistence> S save(S entity) {
        store.add(entity);
        return entity;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends SkillEffectPersistence> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends SkillEffectPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<SkillEffectPersistence> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override @Deprecated
    public SkillEffectPersistence getOne(Long aLong) {
        return null;
    }

    @Override @Deprecated
    public SkillEffectPersistence getById(Long aLong) {
        return null;
    }

    @Override
    public SkillEffectPersistence getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends SkillEffectPersistence> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends SkillEffectPersistence> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends SkillEffectPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends SkillEffectPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends SkillEffectPersistence> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends SkillEffectPersistence> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends SkillEffectPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }


    @Override
    public <S extends SkillEffectPersistence> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<SkillEffectPersistence> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<SkillEffectPersistence> findAll() {
        return List.of();
    }

    @Override
    public List<SkillEffectPersistence> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(SkillEffectPersistence entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends SkillEffectPersistence> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<SkillEffectPersistence> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<SkillEffectPersistence> findAll(Pageable pageable) {
        return null;
    }
}