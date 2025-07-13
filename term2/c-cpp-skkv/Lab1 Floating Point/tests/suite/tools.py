from typing import List

# Escape single string.
def escape(raw: str) -> str:
	escaped = ""
	for c in raw:
		match c:
			case '\t': escaped += "\\t"
			case '\n': escaped += "\\n"
			case '\r': escaped += "\\r"
			case '\\': escaped += "\\\\"
			case _: escaped += c
	return escaped

# Escape multi strings with delim into one string.
def escape_multi(raw: List[str], delim: str) -> str:
	escaped_raw = [escape(s) for s in raw]
	escaped = delim.join(escaped_raw)
	return escaped

# Escape multi strings as "<str> OR <str> ...".
def escape_multi_or(raw: List[str]) -> str:
	return escape_multi(raw, " OR ")
