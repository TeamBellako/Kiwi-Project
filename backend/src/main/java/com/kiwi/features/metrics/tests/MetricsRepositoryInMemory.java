package com.kiwi.features.metrics.tests;

import com.kiwi.features.metrics.controllers.MetricsRepository;
import com.kiwi.features.metrics.data.MetricsPersistence;
import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

public class MetricsRepositoryInMemory implements MetricsRepository {
    private final HashMap<UsersPersistence, Set<MetricsPersistence>> metrics = new HashMap<>();

    public Optional<MetricsPersistence> findByUserAndDate(UsersPersistence usersPersistence, LocalDate date) {
        if (metrics.containsKey(usersPersistence)) {
            return metrics.get(usersPersistence).stream()
                    .filter(metric -> metric.getDate().equals(date))
                    .findFirst();
        }
        return Optional.empty();
    }

    public <S extends MetricsPersistence> S saveAndFlush(S entity) {
        UsersPersistence user = entity.getUser();
        LocalDate date = entity.getDate();

        metrics.putIfAbsent(user, new HashSet<>());
        Set<MetricsPersistence> userMetrics = metrics.get(user);
        Optional<MetricsPersistence> existingMetric = userMetrics.stream()
                .filter(metric -> metric.getDate().equals(date))
                .findFirst();

        existingMetric.ifPresent(userMetrics::remove);
        userMetrics.add(entity);
        return entity;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends MetricsPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<MetricsPersistence> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public MetricsPersistence getOne(Long aLong) {
        return null;
    }

    @Override
    public MetricsPersistence getById(Long aLong) {
        return null;
    }

    @Override
    public MetricsPersistence getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends MetricsPersistence> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends MetricsPersistence> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends MetricsPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends MetricsPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends MetricsPersistence> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends MetricsPersistence> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends MetricsPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends MetricsPersistence> S save(S entity) {
        return null;
    }

    @Override
    public <S extends MetricsPersistence> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<MetricsPersistence> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<MetricsPersistence> findAll() {
        return List.of();
    }

    @Override
    public List<MetricsPersistence> findAllById(Iterable<Long> longs) {
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
    public void delete(MetricsPersistence entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends MetricsPersistence> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<MetricsPersistence> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<MetricsPersistence> findAll(Pageable pageable) {
        return null;
    }
}
