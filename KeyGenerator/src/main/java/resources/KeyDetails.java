package resources;

import java.util.Objects;

public class KeyDetails implements Comparable<KeyDetails> {

	private String key;
	private Long timeStamp;

	public void settimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Long gettimeStamp() {
		return timeStamp;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public int compareTo(KeyDetails o) {
		if (this.timeStamp > o.timeStamp) {
			return -1;
		} else if (this.timeStamp < o.timeStamp)
			return 1;
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		KeyDetails keyDetails = (KeyDetails) obj;
		if (keyDetails.getKey() == this.key)
			return true;

		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return Objects.hash(key);
	}

	@Override
	public String toString() {
		return "KeyDetails [key=" + key + ", timeStamp=" + timeStamp + "]";
	}

}