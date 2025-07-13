package markup;

import java.util.List;
import java.util.ArrayList;

public class Emphasis implements Element {
	List<Element> elements = new ArrayList<>();
	public Emphasis(List<Element> elements) {
		this.elements = elements;
	}
	@Override
	public void toMarkdown(StringBuilder result) {
		result.append("*");
		for (Element element : elements) {
			element.toMarkdown(result);
		}
		result.append("*");
	}
	@Override
	public void toBBCode(StringBuilder result) {
		result.append("[i]");
		for (Element element : elements) {
			element.toBBCode(result);
		}
		result.append("[/i]");
	}
}