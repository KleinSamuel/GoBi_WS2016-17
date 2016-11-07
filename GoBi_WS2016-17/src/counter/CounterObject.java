package counter;

public class CounterObject {

	private String id;

	public CounterObject(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public String getId() {
		return id;
	}

}
