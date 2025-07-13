#ifndef FFTW_OPERATIONS_H
#define FFTW_OPERATIONS_H

#include <stddef.h>

int perform_fftw_operations(double* data1, double* data2, size_t sz1, size_t sz2, int* delta_samples);
void free_resources(double* data1, double* data2);

#endif /* FFTW_OPERATIONS_H */
