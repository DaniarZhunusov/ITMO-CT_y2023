import os
import subprocess
import time
from typing import List

from suite import config
from suite import asserts
from suite import results

# Tests.

class IOTest:
	def __init__(self, input: str, expected_result: asserts.Expected, timeout: int = config.DEFAULT_TIMEOUT, name: str = None, categories: List[str] = []):
		self.input = (' '.join(input) + "\n").encode("ascii")
		self.expected_result = expected_result
		self.timeout = timeout
		self.name = name
		self.categories = categories

	def run(self, filename: str):
		executable = os.path.abspath(filename)
		start = time.time_ns() // 1000000
		program = subprocess.Popen([executable], stdout = subprocess.PIPE, stdin = subprocess.PIPE, stderr = subprocess.PIPE, universal_newlines = True)
		try:
			output, error = program.communicate(self.input.decode("utf-8"), timeout = self.timeout)
			end = time.time_ns() // 1000000
			result = asserts.Actual(output, error, program.returncode)
			return self.expected_result.compare(result, end - start, self.name, self.input.decode("ascii"), self.categories)
		except subprocess.TimeoutExpired:
			program.kill()
			end = time.time_ns() // 1000000
			return results.TestResult(False, self.name, self.expected_result.is_success, self.input.decode("ascii"), None, self.expected_result.stdout, self.expected_result.is_success != True, end - start, "Timeout.", categories = self.categories)

class CmdTest:
	def __init__(self, input: List[str], expected_result: asserts.Expected, timeout: int = config.DEFAULT_TIMEOUT, name: str = None, categories: List[str] = []):
		self.input = input
		self.expected_result = expected_result
		self.timeout = timeout
		self.name = name
		self.categories = categories

	def run(self, filename: str):
		executable = os.path.abspath(filename)
		start = time.time_ns() // 1000000
		program = subprocess.Popen([executable] + self.input, stdout = subprocess.PIPE, stdin = subprocess.PIPE, stderr = subprocess.PIPE, universal_newlines = True)
		try:
			output, error = program.communicate(timeout = self.timeout)
			end = time.time_ns() // 1000000
			result = asserts.Actual(output, error, program.returncode)
			return self.expected_result.compare(result, end - start, self.name, (' '.join(self.input)), self.categories)
		except subprocess.TimeoutExpired:
			program.kill()
			end = time.time_ns() // 1000000
			return results.TestResult(False, self.name, self.expected_result.is_success, (' '.join(self.input)), None, self.expected_result.stdout, self.expected_result.is_success != True, end - start, "Timeout.", categories = self.categories)
