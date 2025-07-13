import hashlib
import difflib
import sys
from typing import List, Union, Tuple

from suite import tools
from suite import config
from suite import results

# Asserts.

class Expected:
	def __init__(self, stdout: Union[List[str], str] = None, stderr: str = None, is_success: bool = True, is_sha256: bool = False, exitcode: int = 0, show_diff: bool = False):
		self.stdout: Union[List[str], str] = None
		if stdout and not is_sha256:
			if isinstance(stdout, str):
				self.stdout = stdout + ("\n" if not stdout.endswith("\n") else "")
			else:
				self.stdout = [out + "\n" if not out.endswith("\n") else out for out in stdout]
		self.stderr = stderr
		self.is_success = is_success
		self.is_sha256 = is_sha256
		self.exitcode = exitcode
		self.show_diff = show_diff

	def __compare_single(self, actual: str, expected: str, show_diff: bool) -> Tuple[bool, str]:
		if actual == expected:
			return True, None

		if show_diff:
			diff = difflib.unified_diff(
				expected.splitlines(), actual.splitlines(),
				fromfile = 'expected_stdout', tofile = 'actual_stdout'
			)
			ndiff = '\n'.join(diff)
			return False, "Expected:\n\"\"\"\n%s\"\"\"\nbut actual is:\n\"\"\"\n%s\"\"\"Difference (https://docs.python.org/3/library/difflib.html#difflib.unified_diff): \n%s\n" %  (expected, actual, ndiff)
		else:
			return False, "Expected:\n\"\"\"\n%s\"\"\"\nbut actual is:\n\"\"\"\n%s\"\"\"" %  (expected, actual)

	def __compare_multi(self, actual: str, expected: List[str]) -> Tuple[bool, str]:
		if any(elem == actual for elem in expected):
			return True, None

		return False, "None of %s was asserted with %s" % ("[one of ===> " + tools.escape_multi_or(expected) + "]", "[actual ===> " + actual.rstrip() + "]")

	def __compare_impl(self, actual: str, expected: Union[List[str], str], show_diff: bool) -> Tuple[bool, str]:
		if isinstance(expected, str):
			return self.__compare_single(actual, expected, show_diff)
		else:
			return self.__compare_multi(actual, expected)

	def compare(self, other: 'Expected', timer: int, name: str, stdin: str, categories: List[str]) -> results.TestResult:
		expected_stdout = self.stdout
		expected_success = self.is_success
		expected_sha256 = self.is_sha256
		expected_exitcode = self.exitcode
		actual_stdout = other.stdout
		actual_success = other.is_success
		actual_stderr = other.stderr
		actual_exitcode = other.exitcode
		empty_error = actual_stderr == None or actual_stderr == ""

		if not expected_success and actual_success:
			return results.TestResult(False, name, expected_success, stdin, actual_stdout, expected_stdout, expected_success != True, timer, actual_exitcode, "Program returns ERROR_CODE = 0.")

		if expected_success and not actual_success:
			print("====> FAILING \"%s\"... STDERR:\n\"\"\"\n%s\"\"\"" % (name, actual_stderr), file = sys.stderr)
			return results.TestResult(False, name,
                              expected_success, stdin, actual_stdout,
                              expected_stdout, expected_success != True,
                              timer, actual_exitcode, error_message = "Program should not fail, returns ERROR_CODE != 0.", categories = categories)

		if not expected_success:
			if empty_error:
				return results.TestResult(False, name,
                            expected_success, stdin, actual_stdout,
                            expected_stdout, empty_error,
                            timer, actual_exitcode, "Standard error output is empty.", categories = categories)
			if actual_stdout != None and actual_stdout != "":
				print("====> FAILING \"%s\"... STDERR:\n\"\"\"\n%s\"\"\"" % (name, actual_stderr), file = sys.stderr)
				return results.TestResult(False, name,
                              expected_success, stdin, actual_stdout,
                              expected_stdout, empty_error,
                              timer, actual_exitcode, "On error program should not writing anything to standard output.", categories = categories)
			if actual_exitcode != expected_exitcode:
				print("====> FAILING \"%s\"... STDERR:\n\"\"\"\n%s\"\"\"" % (name, actual_stderr), file = sys.stderr)
				return results.TestResult(False, name,
                              expected_success, stdin, actual_stdout,
                              expected_stdout, empty_error,
                              timer, actual_exitcode, "Program returns %d, but should return %d." % (actual_exitcode, expected_exitcode), categories = categories)
			return results.TestResult(True, name,
                             expected_success, stdin, actual_stdout,
                             expected_stdout, empty_error,
                             timer, actual_exitcode, categories = categories
            )

		if expected_sha256:
			actual_stdout = hashlib.sha256(actual_stdout.encode("utf-8")).hexdigest()
			if actual_stdout != expected_stdout:
				return results.TestResult(False, name,
                              expected_success, stdin, actual_stdout,
                              expected_stdout, empty_error,
                              timer, actual_exitcode, "SHA-256 from expected output (%s) not equals to actual (%s)." % (expected_stdout, actual_stdout), categories = categories)
			else:
				return results.TestResult(True, name,
                              expected_success, stdin, actual_stdout,
                              expected_stdout, empty_error,
                              timer, actual_exitcode, categories = categories)

		ok, err = self.__compare_impl(actual_stdout, expected_stdout, self.show_diff)
		if not ok:
			print("====> FAILING \"%s\"... STDERR:\n\"\"\"\n%s\"\"\"" % (name, actual_stderr), file = sys.stderr)
		return results.TestResult(ok, name, expected_success, stdin, actual_stdout, expected_stdout, empty_error, timer, actual_exitcode, err, categories)

class Actual(Expected):
	def __init__(self, stdout: str, stderr: str, exitcode: int):
		Expected.__init__(self, stdout = stdout, stderr = stderr, is_success = (exitcode == config.EXIT_SUCCESS), exitcode = exitcode)
