package markup;

import java.util.List;
import java.util.ArrayList;

public class Strikeout implements Element {
	List<Element> elements = new ArrayList<>();
	public Strikeout(List<Element> elements) {
		this.elements = elements;
	}
	@Override
	public void toMarkdown(StringBuilder result) {
		result.append("~");
		for (Element element : elements) {
			element.toMarkdown(result);
		}
		result.append("~");
	}
	@Override
	public void toBBCode(StringBuilder result) {
		result.append("[s]");
		for (Element element : elements) {
			element.toBBCode(result);
		}
		result.append("[/s]");
	}
}