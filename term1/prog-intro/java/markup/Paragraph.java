package markup;

import java.util.List;
import java.util.ArrayList;

public class Paragraph implements Element {
	List<Element> elements = new ArrayList<>();
	public Paragraph(List<Element> elements) {
		this.elements = elements;
	}
	@Override
	public void toMarkdown(StringBuilder result) {
		for (Element element : elements) {
			element.toMarkdown(result);
		}
	}
	@Override
	public void toBBCode(StringBuilder result) {
		for (Element element : elements) {
			element.toBBCode(result);
		}
	}
}	