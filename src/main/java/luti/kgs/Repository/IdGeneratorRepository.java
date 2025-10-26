package luti.kgs.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import luti.kgs.Entity.IdGenerator;

@Repository
public interface IdGeneratorRepository extends JpaRepository<IdGenerator, String> {

	/**
	 * name(시퀀스 이름)에 해당하는 row를 조회하면서 쓰기 락(PESSIMISTIC_WRITE)을 건다.
	 * 트랜잭션 내에서 호출 시 다른 트랜잭션의 접근을 차단한다.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT g FROM IdGenerator g WHERE g.name = :name")
	Optional<IdGenerator> findByNameForUpdate(@Param("name") String name);
}
