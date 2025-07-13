#include "return_codes.h"

#include <inttypes.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

typedef struct Nums
{
	int8_t sign_bit, offset, mant_high, mant_low, mant_offset, exp_offset;
	int16_t exp;
	uint64_t mant;
} Nums;

void normalized(Nums* num, uint8_t bitshift);
void round_mantissa(Nums* num, char rounding_mode);
bool check_nan(Nums* num);
bool check_inf(Nums* num);
bool check_zero(Nums* num);
void print_zero(Nums* num);
void print_inf(Nums* num);
void print_number(Nums* num, char rounding_mode);

void create(Nums* num, uint32_t input, char format)
{
	num->mant_high = 0;
	num->mant_low = 0;

	switch (format)
	{
	case 'f':	 // single precision
		num->mant_offset = 23;
		num->exp_offset = 8;
		num->offset = 1;
		break;
	case 'h':	 // half precision
		num->mant_offset = 10;
		num->exp_offset = 5;
		num->offset = 2;
		break;
	}

	num->sign_bit = (input >> (num->mant_offset + num->exp_offset)) & 0x1;

	uint32_t exp_mask = (1u << num->exp_offset) - 1;
	num->exp = (input >> num->mant_offset) & exp_mask;

	uint32_t mant_mask = (1u << num->mant_offset) - 1;
	num->mant = input & mant_mask;

	if (num->exp == 0)
	{
		num->exp += 1;
	}
	else
	{
		num->mant |= (1u << num->mant_offset);
	}
	int bias = (1 << (num->exp_offset - 1)) - 1;
	num->exp -= bias;
	normalized(num, 1);
}

int parse_args(int argc, char* argv[], char* format, char* rounding_mode)
{
	if (argc != 4 && argc != 6)
	{
		fprintf(stderr, "Error: Arguments invalid\n");
		return ERROR_ARGUMENTS_INVALID;
	}

	*format = argv[1][0];
	if (argv[1][0] == '\0' || argv[1][1] != '\0')
	{
		fprintf(stderr, "Error: Invalid format (must be a single character)\n");
		return ERROR_ARGUMENTS_INVALID;
	}

	switch (*format)
	{
	case 'f':	 // single-precision
	case 'h':	 // half-precision
		break;
	default:
		fprintf(stderr, "Error: Invalid format (use 'f' for single-precision or 'h' for half-precision)\n");
		return ERROR_ARGUMENTS_INVALID;
	}

	int round_mode;
	if (argv[2][0] == '\0' || argv[2][1] != '\0' || sscanf(argv[2], "%d", &round_mode) != 1 || round_mode < 0 || round_mode > 3)
	{
		fprintf(stderr, "Error: Invalid rounding mode (use 0, 1, 2, or 3)\n");
		return ERROR_ARGUMENTS_INVALID;
	}
	*rounding_mode = round_mode;

	if (argc == 6)
	{
		char operation = argv[4][0];
		if (argv[4][0] == '\0' || argv[4][1] != '\0')
		{
			fprintf(stderr, "Error: Operation must be a single character\n");
			return ERROR_ARGUMENTS_INVALID;
		}

		switch (operation)
		{
		case '+':
		case '-':
		case '*':
		case '/':
			break;
		default:
			fprintf(stderr, "Error: Invalid operation (use +, -, *, or /)\n");
			return ERROR_ARGUMENTS_INVALID;
		}
	}
	return SUCCESS;
}

Nums multiply(Nums* num1, Nums* num2)
{
	Nums result;
	result.mant_offset = num1->mant_offset;
	result.exp_offset = num1->exp_offset;
	result.offset = num1->offset;

	if (check_nan(num1) || check_nan(num2))
	{
		result.exp = (1 << (result.exp_offset - 1));
		result.mant = 1;
		return result;
	}

	if (check_inf(num1) || check_inf(num2))
	{
		if (check_zero(num1) || check_zero(num2))
		{
			result.exp = (1 << (result.exp_offset - 1));
			result.mant = 1;
			return result;
		}
		else
		{
			result.exp = (1 << (result.exp_offset - 1));
			result.mant = 0;
			result.sign_bit = num1->sign_bit ^ num2->sign_bit;
			return result;
		}
	}

	if (check_zero(num1) || check_zero(num2))
	{
		result.exp = 0;
		result.mant = 0;
		result.sign_bit = num1->sign_bit ^ num2->sign_bit;
		return result;
	}

	result.sign_bit = num1->sign_bit ^ num2->sign_bit;

	uint64_t product_mant = num1->mant * num2->mant;

	if (product_mant >> (result.mant_offset + 1))
	{
		product_mant >>= 1;
		result.exp = num1->exp + num2->exp + 1;
	}
	else
	{
		result.exp = num1->exp + num2->exp;
	}
	result.mant = product_mant;
	normalized(&result, 0);
	return result;
}

Nums divide(Nums* num1, Nums* num2)
{
	Nums result;
	result.mant_offset = num1->mant_offset;
	result.exp_offset = num1->exp_offset;
	result.offset = num1->offset;

	if (check_zero(num2))
	{
		if (check_zero(num1))
		{
			result.exp = (1 << (result.exp_offset - 1));
			result.mant = 1;
			return result;
		}
		else
		{
			result.sign_bit = num1->sign_bit ^ num2->sign_bit;
			result.exp = (1 << (result.exp_offset - 1));
			result.mant = 0;
			return result;
		}
	}

	if (check_inf(num1) && check_inf(num2))
	{
		result.exp = (1 << (result.exp_offset - 1));
		result.mant = 1;
		return result;
	}

	result.sign_bit = num1->sign_bit ^ num2->sign_bit;
	result.mant = (num1->mant << (num1->mant_offset + 1)) / num2->mant;
	int8_t shifts = !(result.mant >> (num1->mant_offset + 1));
	result.exp = num1->exp - num2->exp - shifts;
	normalized(&result, 0);
	return result;
}

Nums addition(Nums* num1, Nums* num2)
{
	Nums result;
	result.mant_offset = num1->mant_offset;
	result.exp_offset = num1->exp_offset;
	result.offset = num1->offset;

	if (check_nan(num1) || check_nan(num2))
	{
		result.exp = (1 << (result.exp_offset - 1));
		result.mant = 1;
		return result;
	}

	if (check_zero(num1) && check_zero(num2))
	{
		result.sign_bit = 0;
		result.exp = 0;
		result.mant = 0;
		return result;
	}

	if (check_inf(num1) || check_inf(num2))
	{
		if (check_inf(num1) && check_inf(num2) && num1->sign_bit != num2->sign_bit)
		{
			result.exp = (1 << (result.exp_offset - 1));
			result.mant = 1;
		}
		else
		{
			result.sign_bit = check_inf(num1) ? num1->sign_bit : num2->sign_bit;
			result.exp = (1 << (result.exp_offset - 1));
			result.mant = 0;
		}
		return result;
	}

	if (num1->exp > num2->exp)
	{
		int shift = num1->exp - num2->exp;
		num2->mant >>= shift;
		result.exp = num1->exp;
	}
	else
	{
		int shift = num2->exp - num1->exp;
		num1->mant >>= shift;
		result.exp = num2->exp;
	}

	if (num1->sign_bit == num2->sign_bit)
	{
		result.mant = num1->mant + num2->mant;
		result.sign_bit = num1->sign_bit;
	}
	else
	{
		if (num1->mant > num2->mant)
		{
			result.mant = num1->mant - num2->mant;
			result.sign_bit = num1->sign_bit;
		}
		else
		{
			result.mant = num2->mant - num1->mant;
			result.sign_bit = num2->sign_bit;
		}
	}

	result.mant_high = (result.mant >> (result.mant_offset + 1)) & 1;

	normalized(&result, 1);
	return result;
}

Nums subtract(Nums* num1, Nums* num2)
{
	Nums result;
	result.mant_offset = num1->mant_offset;
	result.exp_offset = num1->exp_offset;
	result.offset = num1->offset;

	if (check_nan(num1) || check_nan(num2))
	{
		result.exp = (1 << (result.exp_offset - 1));
		result.mant = 1;
		return result;
	}

	if (check_zero(num1) && check_zero(num2))
	{
		result.sign_bit = 0;
		result.exp = 0;
		result.mant = 0;
		return result;
	}

	if (check_inf(num1) || check_inf(num2))
	{
		if (check_inf(num1) && check_inf(num2) && num1->sign_bit == num2->sign_bit)
		{
			result.exp = (1 << (result.exp_offset - 1));
			result.mant = 1;
		}
		else
		{
			result.sign_bit = check_inf(num1) ? num1->sign_bit : !num2->sign_bit;
			result.exp = (1 << (result.exp_offset - 1));
			result.mant = 0;
		}
		return result;
	}

	if (num1->exp > num2->exp)
	{
		int shift = num1->exp - num2->exp;
		num2->mant >>= shift;
		result.exp = num1->exp;
	}
	else
	{
		int shift = num2->exp - num1->exp;
		num1->mant >>= shift;
		result.exp = num2->exp;
	}

	if (num1->sign_bit != num2->sign_bit)
	{
		result.mant = num1->mant + num2->mant;
		result.sign_bit = num1->sign_bit;
	}
	else
	{
		if (num1->mant > num2->mant)
		{
			result.mant = num1->mant - num2->mant;
			result.sign_bit = num1->sign_bit;
		}
		else
		{
			result.mant = num2->mant - num1->mant;
			result.sign_bit = !num1->sign_bit;
		}
	}

	result.mant_high = (result.mant >> (result.mant_offset + 1)) & 1;
	normalized(&result, 1);

	return result;
}

void normalized(Nums* num, uint8_t bitshift)
{
	if (check_nan(num) || check_inf(num) || check_zero(num))
		return;

	while ((num->mant >> num->mant_offset) >= 2)
	{
		num->mant_high = num->mant & 1;
		num->mant_low = num->mant_high && (num->mant >> (num->mant_offset + 1)) != 1;
		num->mant >>= 1;
		num->exp += bitshift;
	}

	while ((num->mant >> num->mant_offset) == 0)
	{
		num->mant <<= 1;
		num->exp -= bitshift;
	}
}

bool check_inf(Nums* num)
{
	return !(num->mant % (1 << num->mant_offset)) && (num->exp == (1 << (num->exp_offset - 1)));
}

void print_inf(Nums* num)
{
	if (num->sign_bit)
	{
		printf("-inf\n");
	}
	else
	{
		printf("inf\n");
	}
}

bool check_nan(Nums* num)
{
	return (num->exp == (1 << (num->exp_offset - 1))) && (num->mant % (1 << num->mant_offset));
}

void print_number(Nums* num, char rounding_mode)
{
	if (check_nan(num))
	{
		printf("nan\n");
		return;
	}
	else if (check_inf(num))
	{
		print_inf(num);
		return;
	}
	else if (check_zero(num))
	{
		print_zero(num);
		return;
	}
	if (num->sign_bit == 0 && num->mant == 0 && num->exp == 0)
	{
		print_zero(num);
		return;
	}

	if (num->sign_bit)
		printf("-");

	round_mantissa(num, rounding_mode);
	num->mant ^= 1 << (num->mant_offset);
	num->mant <<= num->offset;

	printf("0x1.");
	printf("%0*" PRIx64, (num->mant_offset + num->offset) / 4, num->mant);
	printf("p%+" PRId32 "\n", num->exp);
}

void round_mantissa(Nums* num, char rounding_mode)
{
	uint64_t guard_bit = (num->mant >> (num->mant_offset)) & 1;
	uint64_t sticky_bits = num->mant & ((1u << num->mant_offset) - 1);

	switch (rounding_mode)
	{
	case 1:
		num->mant += (num->mant_high == 1);
		break;
	case 2:
		num->mant += (!num->sign_bit && (guard_bit || sticky_bits));
		break;
	case 3:
		num->mant += (num->sign_bit && (guard_bit || sticky_bits));
		break;
	}
}

bool check_zero(Nums* num)
{
	return !(num->mant % (1 << num->mant_offset)) && (num->exp == 2 - (1 << (num->exp_offset - 1)));
}

void print_zero(Nums* num)
{
	num->exp = 0;
	const char* format = (num->mant_offset == 10) ? "0x0.000p+0\n" : "0x0.000000p+0\n";
	printf("%s", format);
}

int main(int argc, char* argv[])
{
	char format;
	char rounding_mode;
	int parse = parse_args(argc, argv, &format, &rounding_mode);
	if (parse != SUCCESS)
	{
		return parse;
	}
	if (argc == 6)
	{
		uint32_t number1, number2;
		char operation = argv[4][0];

		if (sscanf(argv[3], "%" SCNx32, &number1) != 1 || sscanf(argv[5], "%" SCNx32, &number2) != 1)
		{
			fprintf(stderr, "Error: Invalid number format\n");
			return ERROR_ARGUMENTS_INVALID;
		}
		Nums num1, num2;
		create(&num1, number1, format);
		create(&num2, number2, format);
		Nums multiply_res, division_res, addition_res, subtract_res;
		switch (operation)
		{
		case '*':
			multiply_res = multiply(&num1, &num2);
			print_number(&multiply_res, rounding_mode);
			break;
		case '/':
			division_res = divide(&num1, &num2);
			print_number(&division_res, rounding_mode);
			break;
		case '+':
			addition_res = addition(&num1, &num2);
			print_number(&addition_res, rounding_mode);
			break;
		case '-':
			subtract_res = subtract(&num1, &num2);
			print_number(&subtract_res, rounding_mode);
			break;
		default:
			fprintf(stderr, "Invalid operations\n");
			return ERROR_ARGUMENTS_INVALID;
		}
	}
	else
	{
		uint32_t number;
		if (sscanf(argv[3], "%" SCNx32, &number) != 1)
		{
			fprintf(stderr, "Error: Invalid number format\n");
			return ERROR_ARGUMENTS_INVALID;
		}
		Nums num;
		create(&num, number, format);
		print_number(&num, rounding_mode);
	}
	return SUCCESS;
}
