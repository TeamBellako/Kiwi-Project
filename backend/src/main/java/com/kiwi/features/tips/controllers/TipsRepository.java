package com.kiwi.features.tips.controllers;

import com.kiwi.features.localization.LocalizationQueryRewriter;
import com.kiwi.features.tips.data.TipPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TipsRepository extends JpaRepository<TipPersistence, Long> {

	interface LocalizedTip {
		String getTitle();
		String getText();
		String getReadMoreURL();
	}

	@Query(value = """
			SELECT title_en AS title, text_en AS text, read_more_url AS readMoreURL
			FROM tips
			WHERE id = :id
			""", nativeQuery = true, queryRewriter = LocalizationQueryRewriter.class)
	Optional<LocalizedTip> findLocalizedById(@Param("id") Long id);
}
