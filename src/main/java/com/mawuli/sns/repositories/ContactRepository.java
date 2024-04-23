package com.mawuli.sns.repositories;

import com.mawuli.sns.domain.entities.Contact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContactRepository extends CrudRepository<Contact, Integer>, PagingAndSortingRepository<Contact, Integer> {
}
