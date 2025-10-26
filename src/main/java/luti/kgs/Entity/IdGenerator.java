package luti.kgs.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import luti.kgs.Web.KeyBlock;

@Entity
public class IdGenerator {

	@Id
	private String name;

	private long currentMax;

	public IdGenerator(String name, Long currentMax) {
		this.name = name;
		this.currentMax = currentMax;
	}

	public IdGenerator() {}

	public String getName() {
		return name;
	}

	public long getCurrentMax() {
		return currentMax;
	}

	public void increase(long blockSize) {
		this.currentMax += blockSize;
	}

	public KeyBlock nextBlock(long blockSize) {
		long start = currentMax + 1;
		long end = currentMax + blockSize;
		return new KeyBlock(start, end);
	}

}
