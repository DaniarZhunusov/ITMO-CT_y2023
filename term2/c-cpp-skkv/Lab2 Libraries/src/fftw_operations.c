#include "fftw_operations.h"

#include "return_codes.h"

#include <fftw3.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>

int perform_fftw_operations(double* data1, double* data2, size_t sz1, size_t sz2, int* delta_samples)
{
	size_t n = sz1 + sz2 - 1;
	size_t total_size = 6 * n;

	fftw_complex* all_memory = (fftw_complex*)fftw_malloc(sizeof(fftw_complex) * total_size);
	if (all_memory == NULL)
	{
		fprintf(stderr, "Error: failed to allocate memory for FFTW\n");
		return ERROR_NOTENOUGH_MEMORY;
	}

	fftw_complex* a = all_memory;
	fftw_complex* b = all_memory + n;
	fftw_complex* a_fft = all_memory + 2 * n;
	fftw_complex* b_fft = all_memory + 3 * n;
	fftw_complex* c_fft = all_memory + 4 * n;
	fftw_complex* c_ifft = all_memory + 5 * n;

	for (size_t i = 0; i < sz1; ++i)
	{
		a[i][0] = data1[i];
		a[i][1] = 0.0;
	}
	for (size_t i = sz1; i < n; ++i)
	{
		a[i][0] = 0.0;
		a[i][1] = 0.0;
	}
	for (size_t i = 0; i < sz2; ++i)
	{
		b[i][0] = data2[i];
		b[i][1] = 0.0;
	}
	for (size_t i = sz2; i < n; ++i)
	{
		b[i][0] = 0.0;
		b[i][1] = 0.0;
	}

	int codes = SUCCESS;

	fftw_plan p_a_fft = NULL;
	fftw_plan p_b_fft = NULL;
	fftw_plan p_c_ifft = NULL;

	p_a_fft = fftw_plan_dft_1d((int)n, a, a_fft, FFTW_FORWARD, FFTW_ESTIMATE);
	if (p_a_fft == NULL)
	{
		fprintf(stderr, "Error: failed to create FFTW plan for a_fft\n");
		codes = ERROR_NOTENOUGH_MEMORY;
		goto clean;
	}
	p_b_fft = fftw_plan_dft_1d((int)n, b, b_fft, FFTW_FORWARD, FFTW_ESTIMATE);
	if (p_a_fft == NULL)
	{
		fprintf(stderr, "Error: failed to create FFTW plan for b_fft\n");
		codes = ERROR_NOTENOUGH_MEMORY;
		goto clean;
	}
	fftw_execute(p_a_fft);
	fftw_execute(p_b_fft);

	for (size_t i = 0; i < n; ++i)
	{
		double real = a_fft[i][0] * b_fft[i][0] + a_fft[i][1] * b_fft[i][1];
		double imag = a_fft[i][1] * b_fft[i][0] - a_fft[i][0] * b_fft[i][1];
		c_fft[i][0] = real;
		c_fft[i][1] = imag;
	}

	p_c_ifft = fftw_plan_dft_1d((int)n, c_fft, c_ifft, FFTW_BACKWARD, FFTW_ESTIMATE);
	if (p_c_ifft == NULL)
	{
		fprintf(stderr, "Error: failed to create FFTW plan for b_fft\n");
		codes = ERROR_NOTENOUGH_MEMORY;
		goto clean;
	}
	fftw_execute(p_c_ifft);

	for (size_t i = 0; i < n; ++i)
	{
		c_ifft[i][0] /= n;
		c_ifft[i][1] /= n;
	}

	double maxabs = 0;
	size_t ans = 0;
	for (size_t i = 0; i < n; ++i)
	{
		double absval = sqrt(c_ifft[i][0] * c_ifft[i][0] + c_ifft[i][1] * c_ifft[i][1]);
		if (maxabs < absval)
		{
			maxabs = absval;
			ans = i;
		}
	}

	*delta_samples = (int)ans;
	if (*delta_samples >= (int)(n / 2))
	{
		*delta_samples -= n;
	}
	goto clean;

clean:
	if (p_a_fft != NULL)
		fftw_destroy_plan(p_a_fft);
	if (p_b_fft != NULL)
		fftw_destroy_plan(p_b_fft);
	if (p_c_ifft != NULL)
		fftw_destroy_plan(p_c_ifft);
	fftw_free(all_memory);
	fftw_cleanup();
	return codes;
}

void free_resources(double* data1, double* data2)
{
	if (data1 != NULL)
		free(data1);
	if (data2 != NULL)
		free(data2);
}
