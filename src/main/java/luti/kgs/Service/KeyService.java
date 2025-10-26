package luti.kgs.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import luti.kgs.Entity.IdGenerator;
import luti.kgs.Repository.IdGeneratorRepository;
import luti.kgs.Web.KeyBlock;

@Service
public class KeyService {

	private final IdGeneratorRepository repository;

	// 시퀀스 이름
	private static final String SEQUENCE_NAME = "url";

	// 한 번에 발급할 블록 크기 (1000개)
	private static final int BLOCK_SIZE = 1000;

	public KeyService(IdGeneratorRepository repository) {
		this.repository = repository;
	}

	/**
	 * 트랜잭션 단위로 ID 블록을 안전하게 발급한다.
	 */
	@Transactional
	public KeyBlock allocateNextBlock() {
		// 해당 시퀀스 행을 락 걸고 조회
		IdGenerator generator = repository.findByNameForUpdate(SEQUENCE_NAME)
			.orElseThrow(() -> new IllegalStateException("No sequence found: " + SEQUENCE_NAME));

		// 다음 블록 범위 계산
		long start = generator.getCurrentMax() + 1;
		long end = generator.getCurrentMax() + BLOCK_SIZE;

		// currentMax 갱신
		generator.increase(BLOCK_SIZE);

		// KeyBlock 생성 및 반환
		return new KeyBlock(start, end);
	}
}
