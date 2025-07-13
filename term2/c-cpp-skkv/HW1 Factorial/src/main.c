#include <inttypes.h>
#include <stdint.h>
#include <stdio.h>

uint32_t factorial(uint8_t n)
{
	uint32_t result = 1;
	for (uint8_t i = 2; i <= n; ++i)
	{
		result = (uint32_t)((uint64_t)result * i % 2147483647);
	}
	return result;
}

uint8_t num_digits(uint32_t n)
{
	uint8_t count = 0;
	do
	{
		count++;
		n /= 10;
	} while (n != 0);
	return count;
}

void print_line(uint8_t max_n_digits, uint8_t max_fact_digits)
{
	printf("+");
	for (uint8_t i = 0; i < max_n_digits + 2; i++)
	{
		printf("-");
	}
	printf("+");
	for (uint8_t i = 0; i < max_fact_digits + 2; i++)
	{
		printf("-");
	}
	printf("+\n");
}

void print_table(uint8_t max_n_digits, uint8_t max_fact_digits, int8_t align)
{
	print_line(max_n_digits, max_fact_digits);
	if (align == -1)
	{
		printf("| %-*s | %-*s |\n", max_n_digits, "n", max_fact_digits, "n!");
	}
	else if (align == 0)
	{
		uint8_t n_spaces = max_n_digits - 1;
		uint8_t fact_spaces = max_fact_digits - 2;
		printf("| %*s%s%*s | %*s%s%*s |\n", n_spaces / 2 + (n_spaces % 2), "", "n", n_spaces / 2, "", fact_spaces / 2 + (fact_spaces % 2), "", "n!", fact_spaces / 2, "");
	}
	else
	{
		printf("| %*s | %*s |\n", max_n_digits, "n", max_fact_digits, "n!");
	}
	print_line(max_n_digits, max_fact_digits);
}

void print_row(uint32_t n, uint32_t fact, uint8_t max_n_digits, uint8_t max_fact_digits, int8_t align)
{
	if (align == -1)
	{
		printf("| %-*" PRIu32 " | %-*" PRIu32 " |\n", max_n_digits, n, max_fact_digits, fact);
	}
	else if (align == 0)
	{
		uint8_t n_digits = num_digits(n);
		uint8_t fact_digits = num_digits(fact);
		uint8_t n_left_spaces = (max_n_digits - n_digits) / 2;
		uint8_t n_right_spaces = max_n_digits - n_digits - n_left_spaces;
		uint8_t fact_left_spaces = (max_fact_digits - fact_digits) / 2;
		uint8_t fact_right_spaces = max_fact_digits - fact_digits - fact_left_spaces;

		if ((max_n_digits - n_digits) % 2 != 0)
		{
			n_left_spaces++;
			n_right_spaces--;
		}
		if ((max_fact_digits - fact_digits) % 2 != 0)
		{
			fact_left_spaces++;
			fact_right_spaces--;
		}

		printf("| %*s%" PRIu32 "%*s | %*s%" PRIu32 "%*s |\n", n_left_spaces, "", n, n_right_spaces, "", fact_left_spaces, "", fact, fact_right_spaces, "");
	}
	else
	{
		printf("| %*" PRIu32 " | %*" PRIu32 " |\n", max_n_digits, n, max_fact_digits, fact);
	}
}

void calculate(uint16_t n_start, uint16_t n_end, int8_t align)
{
	uint8_t max_n_digits = 0, max_fact_digits = 2;
	for (uint16_t n = n_start; n <= n_end; n++)
	{
		uint32_t fact = factorial(n);
		uint8_t n_digits = num_digits(n);
		uint8_t fact_digits = num_digits(fact);
		if (n_digits > max_n_digits)
			max_n_digits = n_digits;
		if (fact_digits > max_fact_digits)
			max_fact_digits = fact_digits;
	}

	print_table(max_n_digits, max_fact_digits, align);

	for (uint16_t n = n_start; n <= n_end; n++)
	{
		uint32_t fact = factorial(n);
		print_row(n, fact, max_n_digits, max_fact_digits, align);
	}

	print_line(max_n_digits, max_fact_digits);
}

void backcalculate(uint16_t n_start, uint16_t n_end, int8_t align)
{
	uint16_t max_value = UINT16_MAX;
	uint8_t max_n_digits = 5;
	uint8_t max_fact_digits = 2;

	uint32_t fact = 1;

	for (uint32_t i = 1; i <= max_value; i++)
	{
		fact = (uint32_t)((uint64_t)fact * i % 2147483647);
		uint8_t fact_digits = num_digits(fact);
		if (fact_digits > max_fact_digits)
		{
			max_fact_digits = fact_digits;
		}
	}

	print_table(max_n_digits, max_fact_digits, align);

	fact = 1;
	for (uint32_t i = 1; i <= max_value; i++)
	{
		fact = (uint32_t)((uint64_t)fact * i % 2147483647);
		if (i >= n_start)
		{
			print_row(i, fact, max_n_digits, max_fact_digits, align);
		}
	}

	fact = 1;
	for (uint16_t i = 0; i <= n_end; i++)
	{
		if (i > 0)
		{
			fact = (uint32_t)((uint64_t)fact * i % 2147483647);
		}
		print_row(i, fact, max_n_digits, max_fact_digits, align);
	}

	print_line(max_n_digits, max_fact_digits);
}

int main(void)
{
	int32_t start, end;
	uint16_t n_start, n_end;
	int8_t align;

	if (scanf("%" PRId32 " %" PRId32 " %" SCNd8, &start, &end, &align) != 3)
	{
		fprintf(stderr, "Error: incorrect data entry.\n");
		return 1;
	}

	if (start < 0 || end < 0)
	{
		fprintf(stderr, "Error: n_start or n_end has got negative value.\n");
		return 1;
	}

	if (align < -1 || align > 1)
	{
		fprintf(stderr, "Error: align should be -1, 0 or 1.\n");
		return 1;
	}

	n_start = start;
	n_end = end;

	if (n_start <= n_end)
	{
		calculate(n_start, n_end, align);
	}
	else
	{
		backcalculate(n_start, n_end, align);
	}

	return 0;
}
