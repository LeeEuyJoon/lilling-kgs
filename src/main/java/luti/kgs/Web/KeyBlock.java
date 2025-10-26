package luti.kgs.Web;

public class KeyBlock {

	private long start;

	private long end;

	public KeyBlock(long start, long end) {
		this.start = start;
		this.end = end;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return "KeyBlock{" +
			"start=" + start +
			", end=" + end +
			'}';
	}
}
