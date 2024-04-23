package com.mawuli.sns.repositories;

import com.mawuli.sns.domain.entities.Content;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContentRepository extends CrudRepository<Content, Integer>, PagingAndSortingRepository<Content, Integer> {
}
