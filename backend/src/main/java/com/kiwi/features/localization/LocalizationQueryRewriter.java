package com.kiwi.features.localization;

import com.kiwi.features.users.controllers.UsersRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.QueryRewriter;
import org.springframework.stereotype.Component;

/**
 * Se aplica con {@code @Query(..., queryRewriter = LocalizationQueryRewriter.class)}: la query se escribe
 * con columnas {@code _en} y aquí se adapta al idioma del usuario autenticado.
 */
@Component
public class LocalizationQueryRewriter implements QueryRewriter {

    private final QueryBuildingUtils queryBuildingUtils;
    private final ObjectProvider<UsersRepository> usersRepository;

    public LocalizationQueryRewriter(
            QueryBuildingUtils queryBuildingUtils,
            ObjectProvider<UsersRepository> usersRepository) {
        this.queryBuildingUtils = queryBuildingUtils;
        this.usersRepository = usersRepository;
    }

    @Override
    public String rewrite(String query, Sort sort) {
        UsersRepository repository = usersRepository.getIfAvailable();
        String language = repository == null ? "en" : UserLanguageResolver.resolve(repository);
        return queryBuildingUtils.buildLocalizedQuery(query, language);
    }
}
