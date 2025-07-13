#include "audio_processing.h"
#include "fftw_operations.h"
#include "return_codes.h"
#include <libswresample/swresample.h>

#include <stdlib.h>

int main(int argc, char** argv)
{
	double* data1 = NULL;
	double* data2 = NULL;
	int sr1 = 0, sr2 = 0, ret = 0;
	size_t sz1 = 0, sz2 = 0;
	int delta_samples = 0;

	if (argc == 2)
	{
		ret = get_decode_audio(argv[1], 1, 2, &data1, &sr1, &sz1);
		if (ret)
		{
			free_resources(data1, data2);
			return ret;
		}

		ret = get_decode_audio(argv[1], 2, 2, &data2, &sr2, &sz2);
		if (ret)
		{
			free(data1);
			return ret;
		}
	}
	else if (argc == 3)
	{
		ret = get_decode_audio(argv[1], 1, 1, &data1, &sr1, &sz1);
		if (ret)
		{
			free_resources(data1, data2);
			return ret;
		}

		ret = get_decode_audio(argv[2], 1, 1, &data2, &sr2, &sz2);
		if (ret)
		{
			free(data1);
			return ret;
		}

		if (sr1 != sr2)
		{
			SwrContext* swr_ctx = NULL;
			double* resampled_data = NULL;
			size_t resampled_size = 0;

			int src_rate = (sr1 > sr2) ? sr2 : sr1;
			int dst_rate = (sr1 > sr2) ? sr1 : sr2;
			double* src_data = (sr1 > sr2) ? data2 : data1;
			size_t src_size = (sr1 > sr2) ? sz2 : sz1;

			ret = resample_audio(&swr_ctx, &resampled_data, &resampled_size, src_rate, dst_rate, src_data, src_size);
			if (ret)
			{
				free(data1);
				free(data2);
				return ret;
			}

			if (sr1 > sr2)
			{
				free(data2);
				data2 = resampled_data;
				sz2 = resampled_size;
				sr2 = sr1;
			}
			else
			{
				free(data1);
				data1 = resampled_data;
				sz1 = resampled_size;
				sr1 = sr2;
			}

			swr_free(&swr_ctx);
		}
	}
	else
	{
		printf("Usage: ff2 file1.mp3 [file2.mp3]\n");
		free_resources(data1, data2);
		return ERROR_ARGUMENTS_INVALID;
	}

	if (sz1 == 0 || sz2 == 0)
	{
		fprintf(stderr, "Error: one of the audio files has no data\n");
		free_resources(data1, data2);
		return ERROR_UNSUPPORTED;
	}

	ret = perform_fftw_operations(data1, data2, sz1, sz2, &delta_samples);
	if (ret != SUCCESS)
	{
		free_resources(data1, data2);
		return ret;
	}

	int delta_time_ms = (delta_samples * 1000) / sr1;
	printf("delta: %i samples\nsample rate: %i Hz\ndelta time: %i ms\n", delta_samples, sr1, delta_time_ms);

	free_resources(data1, data2);

	return SUCCESS;
}
