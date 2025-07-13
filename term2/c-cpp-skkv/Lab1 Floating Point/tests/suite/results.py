import pyperclip
from typing import List, Union

from suite import tools

# Results.

class TestResult():
	STR_PASSED_TRUE: str = "PASS"
	STR_PASSED_FALSE: str = "FAIL"
	STR_IS_SUCCESS_TRUE: str = "NO"
	STR_IS_SUCCESS_FALSE: str = "YES"
	STR_EMPTY_ERROR_TRUE: str = "<empty>"
	STR_EMPTY_ERROR_FALSE: str = "<not empty>"

	def __init__(self, passed: bool, name: str, is_success: bool, input: str, actual_output: str, expected_output: Union[List[str], str], empty_error: bool, timer: int, exitcode: int, error_message: str = None, categories: List[str] = []):
		self.passed = passed
		self.name = "<no name>"
		if name:
			self.name = name
		self.is_success = is_success
		self.input = tools.escape(input)
		self.actual_output = "<no output>"
		if actual_output:
			self.actual_output = tools.escape(actual_output)
		self.expected_output = "<no reference>"
		if expected_output:
			if isinstance(expected_output, str):
				self.expected_output = tools.escape(expected_output)
			else:
				self.expected_output = tools.escape_multi_or(expected_output)
		self.empty_error = empty_error
		self.timer = timer
		self.exitcode = exitcode
		self.error_message = error_message
		self.categories = categories

	def __print_end(self, end: bool):
		if end:
			print(" |")
		else:
			print(" ", end = "")

	def __print_str(self, string: str, width: bool, end: bool):
		print("| ", end = "")
		print(string, " " * (width - len(string)), end = "" , sep = "")
		self.__print_end(end)

	def __print_bool(self, flag: bool, true_str: str, false_str: str, width: int, end: bool):
		if flag:
			self.__print_str(true_str, width, end)
		else:
			self.__print_str(false_str, width, end)

	def print_passed(self, width: int, end = False):
		self.__print_bool(self.passed, TestResult.STR_PASSED_TRUE, TestResult.STR_PASSED_FALSE, width, end)

	def print_name(self, width: int, end = False):
		self.__print_str(self.name, width, end)

	def print_is_success(self, width: int, end = False):
		self.__print_bool(self.is_success, TestResult.STR_IS_SUCCESS_TRUE, TestResult.STR_IS_SUCCESS_FALSE, width, end)

	def print_input(self, width: int, end = False):
		self.__print_str(self.input, width, end)

	def print_actual_output(self, width: int, end = False):
		self.__print_str(self.actual_output, width, end)

	def print_expected_output(self, width: str, end = False):
		self.__print_str(self.expected_output, width, end)

	def print_empty_error(self, width: str, end = False):
		self.__print_bool(self.empty_error, TestResult.STR_EMPTY_ERROR_TRUE, TestResult.STR_EMPTY_ERROR_FALSE, width, end)

	def print_exitcode(self, width: str, end = False):
		self.__print_str(str(self.exitcode), width, end)

	def print_timer(self, width: str, end = False):
		self.__print_str(str(self.timer), width, end)

class CategoryResult():
	STR_PASSED = "Status"
	STR_NAME = "Test name"
	STR_IS_SUCCESS = "Should be error?"
	STR_INPUT = "Input"
	STR_ACTUAL_OUTPUT = "Output"
	STR_EXPECTED_OUTPUT = "Reference"
	STR_EMPTY_ERROR = "Is error output empty?"
	STR_EXITCODE = "Exitcode"
	STR_TIMER = "Time (in ms)"

	def __init__(self, results: List[TestResult], category: str):
		self.results = results
		self.passed = sum(1 for result in self.results if result.passed)
		self.total = len(self.results)
		self.category = category
		self.timer = sum([result.timer for result in self.results])

	def __print_cat(self, cat: str, width: int, begin: bool = False, end: bool = False):
		if begin:
			print("| ", end = "")
		print(cat, " " * (width - len(cat)), end = "", sep = "")
		if end:
			print(" |")
		else:
			print(" | ", end = "")

	def width_passed(self):
		return max(len(TestResult.STR_PASSED_TRUE) if result.passed else len(TestResult.STR_PASSED_FALSE) for result in self.results)

	def width_category(self):
		return len(self.category)

	def width_name(self):
		return max(len(result.name) for result in self.results)

	def width_is_success(self):
		return max(len(TestResult.STR_IS_SUCCESS_TRUE) if result.is_success else len(TestResult.STR_IS_SUCCESS_FALSE) for result in self.results)

	def width_input(self):
		return max(len(result.input) for result in self.results)

	def width_actual_output(self):
		return max(len(result.actual_output) for result in self.results)

	def width_expected_output(self):
		return max(len(result.expected_output) for result in self.results)

	def width_empty_error(self):
		return max(len(TestResult.STR_EMPTY_ERROR_TRUE) if result.empty_error else len(TestResult.STR_EMPTY_ERROR_TRUE) for result in self.results)

	def width_exitcode(self):
		return max(len(str(result.exitcode)) for result in self.results)

	def width_timer(self):
		return max(len(str(result.timer)) for result in self.results)

	def print(self, width: int, width_passed: int, width_name: int, width_is_success: int, width_input: int, width_actual_output: int, width_expected_output: int, width_empty_error: int, width_exitcode: int, width_timer: int, header_head: bool = True):
		# number of symbols
		num_cats: int = 9
		in_spaces: int = 2 * num_cats
		count: int = width + in_spaces + num_cats + 1

		# headers
		header_head_str: str = "+" + "-" * (count - 2) + "+"
		header_sub_str: str = "+" + "-" * (width_passed + 2) + "+" + "-" * (width_name + 2) + "+" + "-" * (width_is_success + 2) + "+" + "-" * (width_input + 2) + "+" + "-" * (width_actual_output + 2) + "+" + "-" * (width_expected_output + 2) + "+" + "-" * (width_empty_error + 2) + "+" + "-" * (width_exitcode + 2) + "+" + "-" * (width_timer + 2) + "+"

		# head
		if header_head:
			print(header_head_str)

		# name of category
		print("| ", end = "")
		print(self.category, " " * (count - len(self.category) - 4), end = "" , sep = "")
		print(" |")

		# subcategories
		print(header_sub_str)
		self.__print_cat(CategoryResult.STR_PASSED, width_passed, begin = True)
		self.__print_cat(CategoryResult.STR_NAME, width_name)
		self.__print_cat(CategoryResult.STR_IS_SUCCESS, width_is_success)
		self.__print_cat(CategoryResult.STR_INPUT, width_input)
		self.__print_cat(CategoryResult.STR_ACTUAL_OUTPUT, width_actual_output)
		self.__print_cat(CategoryResult.STR_EXPECTED_OUTPUT, width_expected_output)
		self.__print_cat(CategoryResult.STR_EMPTY_ERROR, width_empty_error)
		self.__print_cat(CategoryResult.STR_EXITCODE, width_exitcode)
		self.__print_cat(CategoryResult.STR_TIMER, width_timer, end = True)
		print(header_sub_str)

		# results
		for result in self.results:
			result.print_passed(width_passed)
			result.print_name(width_name)
			result.print_is_success(width_is_success)
			result.print_input(width_input)
			result.print_actual_output(width_actual_output)
			result.print_expected_output(width_expected_output)
			result.print_empty_error(width_empty_error)
			result.print_exitcode(width_exitcode)
			result.print_timer(width_timer, end = True)
		print(header_sub_str)

class TesterResult():
	def __init__(self, categories: List[CategoryResult]):
		self.categories = categories
		self.passed = sum(category.passed for category in self.categories)
		self.total = sum(category.total for category in self.categories)

	def is_passed(self):
		return self.passed == self.total

	def print_groups(self):
		for category in self.categories:
			print("%s: %d/%d tests passed in %d ms" % (category.category, category.passed, category.total, category.timer))

	def print_table(self):
		if len(self.categories) == 0:
			print("No tests.")
			return
		width_passed = max(len(CategoryResult.STR_PASSED), max(category.width_passed() for category in self.categories))
		width_name = max(len(CategoryResult.STR_NAME), max(category.width_name() for category in self.categories))
		width_is_success = max(len(CategoryResult.STR_IS_SUCCESS), max(category.width_is_success() for category in self.categories))
		width_input = max(len(CategoryResult.STR_INPUT), max(category.width_input() for category in self.categories))
		width_actual_output = max(len(CategoryResult.STR_ACTUAL_OUTPUT), max(category.width_actual_output() for category in self.categories))
		width_expected_output = max(len(CategoryResult.STR_EXPECTED_OUTPUT), max(category.width_expected_output() for category in self.categories))
		width_empty_error = max(len(CategoryResult.STR_EMPTY_ERROR), max(category.width_empty_error() for category in self.categories))
		width_exitcode = max(len(CategoryResult.STR_EXITCODE), max(category.width_exitcode() for category in self.categories))
		width_timer = max(len(CategoryResult.STR_TIMER), max(category.width_timer() for category in self.categories))
		width = width_passed + width_name + width_is_success + width_input + width_actual_output + width_expected_output + width_empty_error + width_exitcode + width_timer
		header_head = True
		for category in self.categories:
			category.print(width, width_passed, width_name, width_is_success, width_input, width_actual_output, width_expected_output, width_empty_error, width_exitcode, width_timer, header_head)
			header_head = False

	def print_counts(self, countable_categories: List[str]):
		if len(self.categories) == 0:
			print("No counts.")
			return

		print("\t".join(map(str, countable_categories)))

		counts = []
		for count in countable_categories:
			total = 0
			passed = 0
			for category in self.categories:
				for result in category.results:
					if count in result.categories:
						total += 1
						if result.passed:
							passed += 1
			counts.append(passed / total)

		counts_str = "\t".join(map(str, counts))
		print(counts_str)

		pyperclip.copy(counts_str)
		print("<copied to clipboard>")
