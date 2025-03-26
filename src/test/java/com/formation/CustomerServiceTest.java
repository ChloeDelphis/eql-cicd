package com.formation;

import com.formation.data.entity.CustomerEntity;
import com.formation.data.repository.CustomerRepository;
import com.formation.service.CustomerService;
import com.formation.web.error.ConflictException;
import com.formation.web.error.NotFoundException;
import com.formation.web.model.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// On teste le service donc on doit mocker le dao

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    // Les users stories
    // En tant que.. Je veux... Afin de...
    // Aident à écrire les tests
    // Given... When... Then

    // On informe le customer service qu'on va lui
    // injecter des mocks
    @InjectMocks
    CustomerService customerService;

    // On mocke le DAO
    @Mock
    CustomerRepository customerRepository;

    /// ///////////
    /// GET ALL ///
    /// ///////////

    @Test
    void getAllCustomers() {

        // Given
        Mockito.doReturn(getMockCustomers(2))
                .when(customerRepository)
                .findAll();

        // When
        List<Customer> customers = customerService.getAllCustomers();

        // Then
        Assertions.assertEquals(2, customers.size());
    }

    // Iterable est implémenté par List, Set
    private Iterable<CustomerEntity> getMockCustomers(int size) {
        List<CustomerEntity> customers = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            CustomerEntity customer = new CustomerEntity(UUID.randomUUID(),
                    "firstName" + i, "lastName" + i,
                    "email" + i, "phone" + i,
                    "adresse" + i);
            customers.add(customer);
        }

        return customers;
    }

    /// //////////////
    /// GET BY ID ///
    /// /////////////

    @Test
    void getCustomerById() {

        // On crée un client mock
        CustomerEntity customerEntity = getMockCustomer();

        // On crée un Optional contenant le client
        Optional<CustomerEntity> optional = Optional.of(customerEntity);

        // On configure le mock pour retourner l'Optional
        Mockito.doReturn(optional)
                .when(customerRepository)
                .findById(customerEntity.getCustomerId());

        // Appelle la méthode à tester
        Customer customer = customerService.getCustomer(customerEntity.getCustomerId().toString());

        // Vérifie que la méthode a retourné un client
        assertNotNull(customer);

        // Vérifie que le client retourné a le bon prénom
        Assertions.assertEquals("Rico", customerEntity.getFirstName());

    }


    @Test
    void getCustomerById_notExists() {

        // On crée un client mock
        CustomerEntity customerEntity = getMockCustomer();

        // On crée un Optional vide
        Optional<CustomerEntity> optional = Optional.empty();

        // On configure le mock pour retourner l'Optional
        Mockito.doReturn(optional)
                .when(customerRepository)
                .findById(customerEntity.getCustomerId());

        // On vérifie que la méthode lève une exception NotFoundException
        Assertions.assertThrows(NotFoundException.class, () ->
                        customerService.getCustomer(customerEntity.getCustomerId().toString()),
                "exception not thrown as expected");
    }

    /// /////////////////
    /// GET BY EMAIL ///
    /// ////////////////

    @Test
    void getCustomerByEmail() {
        // On crée un client mock
        CustomerEntity customerEntity = getMockCustomer();

        // Configure le mock pour retourner le client
        Mockito.doReturn(customerEntity)
                .when(customerRepository)
                .findByEmailAddress(customerEntity.getEmailAddress());

        // Appelle la méthode à tester
        Customer customer = customerService.findByEmailAddress(customerEntity.getEmailAddress());

        // Vérifie que la méthode a retourné un client
        assertNotNull(customer);

        // On vérifie que le client retourné a le bon prénom
        Assertions.assertEquals("Rico", customerEntity.getFirstName());
    }

    /// /////////////////
    /// ADD CUSTOMER ///
    /// ////////////////

    @Test
    void shouldAddCustomerWhenEmailNotExists() {
        // Crée un client mock
        CustomerEntity customerEntity = getMockCustomer();

        // Configure le mock pour retourner null quand on cherche un client par email
        Mockito.doReturn(null)
                .when(customerRepository)
                .findByEmailAddress(customerEntity.getEmailAddress());

        // Configure le mock pour retourner le client quand on sauvegarde
        Mockito.doReturn(customerEntity)
                .when(customerRepository)
                .save(any(CustomerEntity.class));

        // Crée un client à ajouter (DTO)
        Customer customer = new Customer(customerEntity.getCustomerId().toString(),
                customerEntity.getFirstName(), customerEntity.getLastName(),
                customerEntity.getEmailAddress(), customerEntity.getPhoneNumber(),
                customerEntity.getAddress());

        // Appelle la méthode à tester
        customer = customerService.addCustomer(customer);

        // Vérifie que la méthode a retourné un client
        assertNotNull(customer);

        // Vérifie que le client retourné a le bon nom
        Assertions.assertEquals("Mendes", customer.getLastName());
    }


    // Vérifier que customerService.addCustomer()
    // ne permet pas d'ajouter un client existant
    // et lève une exception ConflictException.
    @Test
    void addCustomer_existing() {

        // Crée un client mock
        CustomerEntity entity = getMockCustomer();

        // Configure le mock pour retourner le client quand on cherche par email
        Mockito.doReturn(entity)
                .when(customerRepository)
                .findByEmailAddress(entity.getEmailAddress());

        // Crée un client à ajouter (DTO)
        Customer customer = new Customer(entity.getCustomerId().toString(),
                entity.getFirstName(), entity.getLastName(),
                entity.getEmailAddress(), entity.getPhoneNumber(),
                entity.getAddress());

        // Vérifie que la méthode lève une exception ConflictException
        ConflictException exception = Assertions.assertThrows(ConflictException.class,
                () -> customerService.addCustomer(customer),
                "should have thrown conflict exception");

        Assertions.assertEquals("customer with email already exists", exception.getMessage());
    }


    private CustomerEntity getMockCustomer() {
        CustomerEntity customer = new CustomerEntity(UUID.randomUUID(),
                "Rico", "Mendes", "rico.mendes@gmail.com",
                "01 25 36 54 47", "7 avenue de la Liberté");

        return customer;
    }


}
