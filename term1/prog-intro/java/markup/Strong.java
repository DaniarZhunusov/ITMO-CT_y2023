package markup;

import java.util.List;
import java.util.ArrayList;

public class Strong implements Element {
	List<Element> elements = new ArrayList<>();
	public Strong(List<Element> elements) {
		this.elements = elements;
	}
	@Override
	public void toMarkdown(StringBuilder result) {
		result.append("__");
		for (Element element : elements) {
			element.toMarkdown(result);
		}
		result.append("__");
	}
	@Override
	public void toBBCode(StringBuilder result) {
		result.append("[b]");
		for (Element element : elements) {
			element.toBBCode(result);
		}
		result.append("[/b]");
	}
}