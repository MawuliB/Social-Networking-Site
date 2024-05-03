package com.mawuli.sns.contact;

import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.repositories.ContactRepository;
import com.mawuli.sns.services.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddContactToBlackList() {
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setIsBlacklisted(false);

        when(contactRepository.findById(1)).thenReturn(Optional.of(contact));
        when(contactRepository.save(contact)).thenReturn(contact);

        Contact result = contactService.addContactToBlackList(1);

        assertTrue(result.getIsBlacklisted());
        verify(contactRepository, times(1)).findById(1);
        verify(contactRepository, times(1)).save(contact);
    }

    @Test
    public void testRemoveContactFromBlackList() {
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setIsBlacklisted(true);

        when(contactRepository.findById(1)).thenReturn(Optional.of(contact));
        when(contactRepository.save(contact)).thenReturn(contact);

        Contact result = contactService.removeContactFromBlackList(1);

        assertFalse(result.getIsBlacklisted());
        verify(contactRepository, times(1)).findById(1);
        verify(contactRepository, times(1)).save(contact);
    }
}