import sys
import os

from enum import Enum

from suite.config import *
from suite.experimental import *

# Usage.
if len(sys.argv) < 3:
	print("Usage: python tests.py <program name> <profile name> [<category_name>]*.")
	exit(EXIT_FAILURE)

# Profile set.
class Profile(float, Enum):
	RELEASE = 1
	DEBUG = 1.5
	ADDRESS_SANITIZED = 3.5
	LEAK_SANITIZED = 3
	UNDEFINED_BEHAVIOR_SANITIZED = 2.5
	THREAD_SANITIZED = 15
	MEMORY_SANITIZED = 4.5

def str2profile(s: str) -> Profile:
	match s:
		case 'Release': return Profile.RELEASE
		case 'Debug': return Profile.DEBUG
		case 'AddressSanitized': return Profile.ADDRESS_SANITIZED
		case 'LeakSanitized': return Profile.LEAK_SANITIZED
		case 'UndefinedBehaviorSanitized': return Profile.UNDEFINED_BEHAVIOR_SANITIZED
		case 'ThreadSanitized': return Profile.THREAD_SANITIZED
		case 'MemorySanitized': return Profile.MEMORY_SANITIZED
		case _: raise ValueError('Unsupported profile \"%s\" found' % (s))

# Name of the program being tested.
program_name = sys.argv[1]

# Profile use.
profile = str2profile(sys.argv[2])

# Set timeout.
T = profile.value

if os.name == 'nt':
	T *= 10.0

# Determine, if program is actually exists.
if not os.path.exists(program_name):
	print("File %s not found." % (program_name))
	exit(EXIT_FAILURE)

# Regular expression for output.
REGEX_OUTPUT = r'^delta: ([-]?[0-9]+) samples\nsample rate: ([-]?[0-9]+) Hz\ndelta time: ([-]?[0-9]+) ms\n$'

# Tests.
TESTS_RICK_ROLL_POS = RegexTester("rick rolled (positive tests)", program_name, REGEX_OUTPUT) \
	.add_pass(input = ["test_data/rickroll354_1.mp3", "test_data/rickroll354_cutted.mp3"], expected = [356544, 44100, [8084-1, 8084+1]], categories = ["positive_test", "positive_delta"], timeout = 2 * T, name = "Rick Roll (rickroll354_1 - rickroll354_cutted)") \
	.add_pass(input = ["test_data/rickroll354_cutted.mp3", "test_data/rickroll354_1.mp3"], expected = [-356544, 44100, [-8084-1, -8084+1]], categories = ["positive_test", "negative_delta"], timeout = 2 * T, name = "Rick Roll (rickroll354_cutted - rickroll354_1)") \
	.add_pass(input = ["test_data/rickroll354_2.mp3"],                                     expected = [0, 44100, 0], categories = ["positive_test", "positive_delta"], timeout = 2 * T, name = "Rick Roll (rickroll354_2)")

TESTS_RICK_ROLL_NEG = RegexTester("rick rolled (negative tests)", program_name, REGEX_OUTPUT) \
	.add_fail(input = ["test_data/rickroll354_1.mp3"], exitcode = [4, 5], categories = ["negative_test"], timeout = 2 * T)

# All tests bundle.
suite = AllTester() \
	.add(TESTS_RICK_ROLL_POS) \
	.add(TESTS_RICK_ROLL_NEG)

# Extract tests.
if len(sys.argv) >= 4:
	selected = sys.argv[2:]
	suite.extract_only(selected)

# Test running...
print("LW2 - Libraries")
results = suite.run()

# Show results as "Suite <category>: <number of passed>/<number total> tests passed in <timer> ms".
print("=" * 30)
results.show_categories()

# Show results as "<number of passed>/<number total> tests passed in <timer> ms".
print("=" * 30)
results.show_total()

# Copy to clipboard raw coefficients for resulting table. Only for inspectors.
# results.clip_coefficients()

exit(results.get_verdict())
