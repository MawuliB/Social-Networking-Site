package com.mawuli.sns.repositories;

import com.mawuli.sns.domain.entities.SharedContent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SharedContentRepository extends CrudRepository<SharedContent, Integer>, PagingAndSortingRepository<SharedContent, Integer> {
}
