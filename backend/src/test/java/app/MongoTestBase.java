package app;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class MongoTestBase {

  @Autowired
  protected MongoTemplate mongoTemplate;

  @AfterEach
  void limpiarDB() {
    mongoTemplate.getCollectionNames()
        .forEach(col -> mongoTemplate.dropCollection(col));
  }
}
