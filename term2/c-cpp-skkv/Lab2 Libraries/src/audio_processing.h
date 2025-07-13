#ifndef AUDIO_PROCESSING_H
#define AUDIO_PROCESSING_H

#include <libswresample/swresample.h>

#include <stddef.h>

int get_decode_audio(const char* file_name, int ch_num, int max_num, double** output_data, int* output_sample_rate, size_t* output_size);
int resample_audio(SwrContext** swr_ctx, double** output_data, size_t* output_size, int src_rate, int dst_rate, double* input_data, size_t input_size);

#endif /* AUDIO_PROCESSING_H */
