package com.formation;

import com.formation.service.CustomerService;
import com.formation.web.error.NotFoundException;
import com.formation.web.model.Customer;
import io.micrometer.common.util.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


/// TESTS D'INTEGRATION
/// voir si service fonctionne bien avec repo

// Si un composant fonctionne bien avec un autre

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CustomerServiceIntegrationTest {

    @Autowired
    CustomerService customerService;

    @Test
    void getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();

        Assertions.assertEquals(5, customers.size());
    }

    @Test
    void getCustomer() {
        // On appelle le service pour récupérer un client avec
        // l'identifiant "054b145c-ddbc-4136-a2bd-7bf45ed1bef7"
        Customer customer = customerService.getCustomer("054b145c-ddbc-4136-a2bd-7bf45ed1bef7");

        // On vérifie que le client retourné n'est pas null
        assertNotNull(customer);

        // On vérifie que le prénom du client est bien Cally
        assertEquals("Cally", customer.getFirstName());
    }

    @Test
    void getCustomer_NotFound() {
        // On appelle le service pour récupérer u client avec un id qui n'existe pas
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                        customerService.getCustomer("d972b30f-21cc-411f-b374-685ce23cd317"),
                "should have thrown an exception");

        assertEquals("customer not found with id", exception.getMessage());
    }

    @Test
    void addCustomer () {
        // On crée un nouveau client
        Customer customer = new Customer ("", "John", "Doe", "jdoe@test.com", "555",
                "16 main street");

        customer = customerService.addCustomer(customer);

        assertTrue(StringUtils.isNotBlank(customer.getCustomerId()));

        assertEquals("John", customer.getFirstName());

        customerService.deleteCustomer(customer.getCustomerId());
    }


}
