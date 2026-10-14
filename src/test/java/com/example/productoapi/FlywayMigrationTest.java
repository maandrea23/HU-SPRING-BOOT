package com.example.productoapi;

import com.example.productoapi.repository.CategoryRepository;
import com.example.productoapi.repository.EventRepository;
import com.example.productoapi.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:flyway-migration-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=validate",
        "eventify.seed.enabled=false"
})
class FlywayMigrationTest {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void aplicaLasTresMigracionesYPueblaElCatalogo() {
        assertEquals(200, eventRepository.count());
        assertEquals(10, venueRepository.count());
        assertEquals(7, categoryRepository.count());
    }
}
