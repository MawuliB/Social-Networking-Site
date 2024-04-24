package com.mawuli.sns.repositories;

import com.mawuli.sns.domain.entities.BlackList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BlackListRepository extends CrudRepository<BlackList, Integer>, PagingAndSortingRepository<BlackList, Integer> {
}