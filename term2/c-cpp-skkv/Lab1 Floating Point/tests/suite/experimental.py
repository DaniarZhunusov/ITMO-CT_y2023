import os
import sys
import time
import subprocess
import re
import pyperclip
from enum import Enum
from typing import List, Union, Tuple, Set

from suite import config
from suite import tools

def convert_to_int(raw: str) -> int:
	return int(raw)

def convert_to_float(raw: str) -> float:
	return float(raw)

def convert_to_str(raw: str) -> str:
	return raw

class Strict:
	def __init__(self, expected: Union[int, float, str]):
		self.conv: callable = None
		if isinstance(expected, int):
			self.conv = convert_to_int
		elif isinstance(expected, float):
			self.conv = convert_to_float
		elif isinstance(expected, str):
			self.conv = convert_to_str
		else:
			raise ValueError("Type of expected value is not supported.")
		self.expected = expected

	def __eq__(self, other: str) -> bool:
		converted = self.conv(other)
		return self.expected == converted

	def __ne__(self, other: str) -> bool:
		return not (self == other)

	def to_string(self) -> str:
		return str(self.expected)

class Range:
	def __init__(self, lo: Union[int, float], hi: Union[int, float]):
		self.conv: callable = None
		if isinstance(lo, int):
			self.conv = convert_to_int
		elif isinstance(lo, float):
			self.conv = convert_to_float
		else:
			raise ValueError("Type of expected value is not supported.")
		self.lo = lo
		self.hi = hi

	def __eq__(self, other: str) -> bool:
		converted = self.conv(other)
		return self.hi > converted and self.lo <= converted

	def __ne__(self, other: str) -> bool:
		return not (self == other)

	def to_string(self) -> str:
		return "<in range from %d to %d>" % (self.lo, self.hi)

ExpectedT = Union[Strict, Range]
ExpectedRawT = Union[List[int], List[float], str, int, float]

def expected_from_array(arr: List[ExpectedRawT]) -> List[ExpectedT]:
	return [Range(min(x), max(x)) if isinstance(x, list) else Strict(x) for x in arr]

class Errno(Enum):
	ERROR_SUCCESS = 0
	ERROR_SHOULD_PASS = 1
	ERROR_SHOULD_FAIL = 2
	ERROR_STDERR_EMPTY = 3
	ERROR_STDOUT_NOT_EMPTY = 4
	ERROR_STDERR_NOT_EMPTY = 5
	ERROR_EXITCODE = 6
	ERROR_ASSERTION = 7
	ERROR_OUTPUT_FORMAT = 8
	ERROR_TIMEOUT = 9
	ERROR_UNKNOWN = 10

class TestResult:
	def __init__(self, errno: Errno, categories: Set[str], timer: int, stderr: str = None, expected_exitcode: Set[int] = None, actual_exitcode: int = None, assert_pos: int = None, actual_assert: str = None, expected_assert: str = None):
		self.is_pass = errno == Errno.ERROR_SUCCESS
		self.errno = errno
		self.categories = categories
		self.timer = timer
		self.stderr = stderr
		self.expected_exitcode = expected_exitcode
		self.actual_exitcode = actual_exitcode
		self.assert_pos = assert_pos
		self.actual_assert = actual_assert
		self.expected_assert = expected_assert

	def str_error(self) -> str:
		match self.errno:
			case Errno.ERROR_SUCCESS: raise ValueError("Successfully passed should not get error string view.")
			case Errno.ERROR_SHOULD_PASS: return "program should not fail (exitcode = %d)" % (self.actual_exitcode)
			case Errno.ERROR_SHOULD_FAIL: return "program should not return successful exitcode"
			case Errno.ERROR_STDERR_EMPTY: return "standard error output is empty"
			case Errno.ERROR_STDOUT_NOT_EMPTY: return "on error program should not writing anything to standard output"
			case Errno.ERROR_STDERR_NOT_EMPTY: return "on successful program should not writing anything to standard error output"
			case Errno.ERROR_EXITCODE: return "program returns %d, but should return one of %s" % (self.actual_exitcode, str(self.expected_exitcode))
			case Errno.ERROR_ASSERTION: return "assert at extracted positional value [%d] => [actual = %s] vs [expected = %s]" % (self.assert_pos, tools.escape(self.actual_assert), self.expected_assert)
			case Errno.ERROR_OUTPUT_FORMAT: return "output format is incorrect"
			case Errno.ERROR_TIMEOUT: return "timeout"
			case Errno.ERROR_UNKNOWN: return "unknown"
			case _: raise ValueError("Type of errno is not supported.")

	def empty_stderr(self) -> bool:
		return self.stderr == "" or self.stderr is None

class Actual:
	def __init__(self, stdout: str, stderr: str, exitcode: int):
		self.stdout = stdout
		self.stderr = stderr
		self.exitcode = exitcode
		self.error = exitcode != 0

class Expected:
	def __init__(self, regex_pattern: str, expected: List[ExpectedT], fails: bool, exitcode: Union[int, List[int]], categories: Set[str]):
		self.regex_pattern = regex_pattern
		self.expected = expected
		self.fails = fails
		self.exitcode: Set[int] = None
		if isinstance(exitcode, int):
			self.exitcode = {exitcode}
		elif isinstance(exitcode, list):
			self.exitcode = set(exitcode)
		else:
			raise ValueError("Expected as int, or List[int] as exitcode.")
		self.categories = categories

	def __compare_failed(self, actual: Actual, timer_test: int) -> TestResult:
		empty_stderr = actual.stderr == "" or actual.stderr is None
		empty_stdout = actual.stdout == "" or actual.stdout is None

		# CASE: If should fail, then should be error message.
		if empty_stderr:
			return TestResult(errno = Errno.ERROR_STDERR_EMPTY, categories = self.categories, timer = timer_test)

		# CASE: If should fail, then output should be empty.
		if not empty_stdout:
			return TestResult(errno = Errno.ERROR_STDOUT_NOT_EMPTY, categories = self.categories, timer = timer_test, stderr = actual.stderr)

		# CASE: If should fail, then exitcode must be correct.
		if not actual.exitcode in self.exitcode:
			return TestResult(errno = Errno.ERROR_EXITCODE, categories = self.categories, timer = timer_test, stderr = actual.stderr, actual_exitcode = actual.exitcode, expected_exitcode = self.exitcode)

		# Otherwise: test passed.
		return TestResult(errno = Errno.ERROR_SUCCESS, categories = self.categories, timer = timer_test)

	def __compare_passes(self, actual: Actual, timer_test: int) -> TestResult:
		empty_stderr = actual.stderr == "" or actual.stderr is None
		matches = re.search(self.regex_pattern, actual.stdout)

		# CASE: If should pass, then error output should be empty.
		if not empty_stderr:
			return TestResult(errno = Errno.ERROR_STDERR_NOT_EMPTY, categories = self.categories, timer = timer_test, stderr = actual.stderr)

		# CASE: If should pass, then regular expression should matching.
		if matches is None:
			return TestResult(errno = Errno.ERROR_OUTPUT_FORMAT, categories = self.categories, timer = timer_test)

		# CASE: If should pass, then values should be in Strict or Range assert.
		for i in range(len(self.expected)):
			raw_value = matches.group(i + 1)
			expected_value = self.expected[i]
			if expected_value != raw_value:
				return TestResult(errno = Errno.ERROR_ASSERTION, categories = self.categories, timer = timer_test, assert_pos = i, actual_assert = raw_value, expected_assert = expected_value.to_string())

		# Otherwise: test passed.
		return TestResult(errno = Errno.ERROR_SUCCESS, categories = self.categories, timer = timer_test)

	def compare(self, actual: Actual, timer_test: int) -> TestResult:
		# CASE: Should fail, but program returns 0.
		if self.fails and not actual.error:
			return TestResult(errno = Errno.ERROR_SHOULD_FAIL, categories = self.categories, timer = timer_test)

		# CASE: Should not fail, but program doesn't returns 0.
		if not self.fails and actual.error:
			return TestResult(errno = Errno.ERROR_SHOULD_PASS, categories = self.categories, timer = timer_test, stderr = actual.stderr, actual_exitcode = actual.exitcode)

		# CASE: Should fail, then check stdout, stderr and exitcode.
		#                    otherwise, compare with expected.
		if self.fails:
			return self.__compare_failed(actual, timer_test)

		return self.__compare_passes(actual, timer_test)

def get_time() -> int:
	return time.time_ns() // 1000000

class CmdTest:
	def __init__(self, input: List[str], expected: Expected, timeout: int = config.DEFAULT_TIMEOUT, name: str = None):
		self.input = input
		self.expected = expected
		self.timeout = timeout
		self.name = name

	def run(self, filename: str) -> TestResult:
		executable = os.path.abspath(filename)
		start = get_time()
		program = subprocess.Popen([executable] + self.input, stdout = subprocess.PIPE, stdin = subprocess.PIPE, stderr = subprocess.PIPE, universal_newlines = True)
		try:
			output, error = program.communicate(timeout = self.timeout)
			end = get_time()
			timer = end - start
			actual = Actual(output, error, program.returncode)
			return self.expected.compare(actual, timer)
		except subprocess.TimeoutExpired:
			program.kill()
			end = get_time()
			return TestResult(errno = Errno.ERROR_TIMEOUT, categories = self.expected.categories, timer = end - start)
		except Exception as err:
			program.kill()
			end = get_time()
			return TestResult(errno = Errno.ERROR_UNKNOWN, categories = self.expected.categories, timer = end - start, stderr = str(err))

class CategoryResult:
	def __init__(self, category: str, tests: List[TestResult], timer: int):
		self.category = category
		self.tests = tests
		self.timer = timer

	def __str__(self) -> str:
		return "Suite \"%s\": %d/%d tests passed in %d ms" % (self.category, self.passed(), self.total(), self.timer)

	def passed(self) -> int:
		return sum(1 for test in self.tests if test.is_pass)

	def total(self) -> int:
		return len(self.tests)

class RegexTester:
	def __init__(self, category: str, filename: str, regex_pattern: str):
		self.category = category
		self.filename = filename
		self.regex_pattern = regex_pattern
		self.tests: List[CmdTest] = []

	def is_empty(self) -> bool:
		return len(self.tests) == 0

	def extract_only(self, categories: Set[str]) -> 'RegexTester':
		new_tester = RegexTester(self.category, self.filename, self.regex_pattern)
		for test in self.tests:
			if test.expected.categories.issubset(categories):
				new_tester.tests.append(test)
		return new_tester

	def run(self) -> CategoryResult:
		result = []
		start = get_time()
		print("=> Test suite: \"%s\" tests." % (self.category))
		for i, test in enumerate(self.tests):
			n_test: Union[str, int] = test.name
			if n_test is None:
				n_test = i + 1
			print("==> Running test %s" % (str(n_test)))
			test_result = test.run(self.filename)
			if test_result.is_pass:
				print("===> SUCCESS in %d ms" % (test_result.timer))
			else:
				print("===> FAILED")
				print("====> ERROR: %s." % (test_result.str_error()))
				if not test_result.empty_stderr():
					print("[stderr %s]: %s" % (str(n_test), test_result.stderr), file = sys.stderr)
			result.append(test_result)
		end = get_time()
		return CategoryResult(self.category, result, end - start)

	def __add_test(self, input: List[str], expected: Union[List[ExpectedT], List[ExpectedRawT]], fails: bool, timeout: int, exitcode: Union[int, List[int]], categories: List[str], name: str) -> 'RegexTester':
		expected_list = None
		if expected is not None:
			if all(isinstance(item, ExpectedT) for item in expected):
				expected_list = expected
			else:
				expected_list = expected_from_array(expected)
		expected_object = Expected(self.regex_pattern, expected_list, fails, exitcode, set(categories))
		self.tests.append(CmdTest(input, expected_object, timeout, name))
		return self

	def add_pass(self, input: List[str], expected: Union[List[ExpectedT], List[ExpectedRawT]], name: str = None, categories: List[str] = [], timeout: int = config.DEFAULT_TIMEOUT) -> 'RegexTester':
		return self.__add_test(input, expected, False, timeout, config.ERROR_SUCCESS, categories, name)

	def add_fail(self, input: List[str], exitcode: Union[int, List[int]], name: str = None, categories: List[str] = [], timeout: int = config.DEFAULT_TIMEOUT) -> 'RegexTester':
		return self.__add_test(input, None, True, timeout, exitcode, categories, name)

class AllResult:
	def __init__(self, results: List[CategoryResult], timer: int):
		self.results = results
		self.timer = timer

	def __get_total(self) -> int:
		return sum(result.total() for result in self.results)

	def __get_passed(self) -> int:
		return sum(result.passed() for result in self.results)

	def clip_coefficients(self, categories: List[str] = None):
		if categories is None or len(categories) == 0 or len(self.results) == 0:
			return

		print("\t".join(map(str, categories)))

		counts = []
		for count in categories:
			total = 0
			passed = 0
			for category in self.results:
				for result in category.tests:
					if count in result.categories:
						total += 1
						if result.is_pass:
							passed += 1
			counts.append(passed / total)

		counts_str = "\t".join(map(str, counts))
		print(counts_str)

		pyperclip.copy(counts_str)
		print("<copied to clipboard>")

	def get_verdict(self) -> int:
		if self.__get_total() == self.__get_passed():
			return config.EXIT_SUCCESS
		return config.EXIT_FAILURE

	def show_categories(self):
		for result in self.results:
			print(result)

	def show_total(self):
		print("%d/%d tests passed in %d ms." % (self.__get_passed(), self.__get_total(), self.timer))

class AllTester:
	def __init__(self):
		self.testers: List[RegexTester] = []

	def add(self, tester: RegexTester) -> 'AllTester':
		self.testers.append(tester)
		return self

	def extract_only(self, categories: List[str]):
		cats = set(categories)
		new_testers = []
		for tester in self.testers:
			new_tester = tester.extract_only(cats)
			if not new_tester.is_empty():
				new_testers.append(new_tester)
		self.testers = new_testers

	def run(self) -> AllResult:
		start = get_time()
		results = [tester.run() for tester in self.testers]
		end = get_time()
		return AllResult(results, end - start)
