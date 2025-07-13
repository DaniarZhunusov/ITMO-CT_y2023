package markup;

public class Text implements Element {
	private String text; // :NOTE: final
	public Text (String text) {
		this.text = text;
	}
	public void toMarkdown(StringBuilder result) {
		result.append(text);
	}
	public void toBBCode(StringBuilder result) {
		result.append(text);
	}
}
