package trajkovs_CSCI201L_Assignment1;

public class Pair<I1, I2> {
	private I1 i1;
	private I2 i2;
	
	public Pair (I1 i1, I2 i2) {
		this.i1 = i1;
		this.i2 = i2;
	}
	
	public I1 getItem1() { return i1; }
	public I2 getItem2() { return i2; }
	
	public void setItem1(I1 i1) { this.i1 = i1; }
	public void setItem2(I2 i2) { this.i2 = i2; }
}
