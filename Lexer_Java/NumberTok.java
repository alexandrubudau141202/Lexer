public class NumberTok extends Token {
	public final String value;

	public NumberTok(String val){
		super(Tag.NUM);
		this.value = val;
	}

	public String toString(){
		return "<" + Tag.NUM + ", " + value + ">";
	}
}
