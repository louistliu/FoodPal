package server.database;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

/**
 * Verify the validity of the database schema (all tables).
 * This test should be run once.
 */
// https://docs.spring.io/spring-boot/docs/1.1.0.M1/reference/html/howto-database-initialization.html
@DataJpaTest(properties = {"spring.hibernate.dll-auto=validate"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SchemaValidationTest {
    @Test
    public void testSchemaValidity() {
    }
}
