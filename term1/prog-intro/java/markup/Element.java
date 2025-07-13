package markup;

public interface Element {
	void toMarkdown(StringBuilder result);
	void toBBCode(StringBuilder result);
}

