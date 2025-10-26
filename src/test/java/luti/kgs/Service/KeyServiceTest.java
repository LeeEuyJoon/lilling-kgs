package luti.kgs.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import luti.kgs.Entity.IdGenerator;
import luti.kgs.Repository.IdGeneratorRepository;
import luti.kgs.Web.KeyBlock;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
class KeyServiceIntegrationTest {

	// MySQL 컨테이너
	@Container
	private static final MySQLContainer<?> mysqlContainer =
		new MySQLContainer<>("mysql:8.0.36")
			.withDatabaseName("lilling_kgs_test")
			.withUsername("testuser")
			.withPassword("testpass");

	// Spring이 이 DB로 자동 연결되도록 동적으로 속성 주입
	@DynamicPropertySource
	static void registerMySqlProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mysqlContainer::getUsername);
		registry.add("spring.datasource.password", mysqlContainer::getPassword);
		registry.add("spring.datasource.driver-class-name", mysqlContainer::getDriverClassName);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
		registry.add("spring.jpa.show-sql", () -> "true");
	}

	@Autowired
	private KeyService keyService;

	@Autowired
	private IdGeneratorRepository repository;

	private static final String SEQUENCE_NAME = "url";

	@BeforeEach
	void setup() {
		IdGenerator generator = new IdGenerator(SEQUENCE_NAME, 0L);
		repository.save(generator);
	}

	@Test
	@DisplayName("ID 블록을 발급하면 currentMax가 증가하고 반환 범위가 올바르다")
	void allocateNextBlock_updatesCurrentMax() {
		KeyBlock block = keyService.allocateNextBlock();

		assertThat(block.getStart()).isEqualTo(1L);
		assertThat(block.getEnd()).isEqualTo(1000L);

		IdGenerator updated = repository.findById(SEQUENCE_NAME).orElseThrow();
		assertThat(updated.getCurrentMax()).isEqualTo(1000L);
	}

	@Test
	@DisplayName("두 번 호출 시 ID 블록이 연속적으로 발급된다")
	void allocateTwice_generatesSequentialBlocks() {
		KeyBlock first = keyService.allocateNextBlock();
		assertThat(first.getStart()).isEqualTo(1L);
		assertThat(first.getEnd()).isEqualTo(1000L);

		KeyBlock second = keyService.allocateNextBlock();
		assertThat(second.getStart()).isEqualTo(1001L);
		assertThat(second.getEnd()).isEqualTo(2000L);
	}
}