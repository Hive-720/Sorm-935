import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentMethodSyncTest {

    private final static Integer POSTGRES_PORT = 5433;
    private final static Integer POSTGRES_PORT_SECOND = 5432;

    protected static final GenericContainer POSTGRES_CONTAINER = new GenericContainer<>("postgres:17.6")
            .withExposedPorts(POSTGRES_PORT, POSTGRES_PORT_SECOND)
            .withEnv("POSTGRES_PASSWORD", "123")
            .withEnv("POSTGRES_DB", "Sorm")
            .withEnv("POSTGRES_USER", "postgres");

    @BeforeAll
    public static void setUp() {
        try {
            POSTGRES_CONTAINER.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
