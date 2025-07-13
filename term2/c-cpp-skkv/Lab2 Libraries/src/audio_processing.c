#include "audio_processing.h"

#include "return_codes.h"
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/opt.h>
#include <libswresample/swresample.h>

#include <stdio.h>
#include <stdlib.h>

int get_decode_audio(const char* file_name, int ch_num, int max_num, double** output_data, int* output_sample_rate, size_t* output_size)
{
	AVFormatContext* fcnt = NULL;
	AVCodecContext* ccnt = NULL;
	AVFrame* fr = NULL;
	const AVCodec* codec;
	AVPacket packet;
	int channels;
	int code = SUCCESS;

	av_log_set_level(AV_LOG_QUIET);
	fcnt = avformat_alloc_context();
	if (!fcnt)
	{
		fprintf(stderr, "Error: could not allocate format context\n");
		return ERROR_NOTENOUGH_MEMORY;
	}
	int opens = avformat_open_input(&fcnt, file_name, NULL, NULL);
	if (opens < 0)
	{
		switch (-opens)
		{
		case AVERROR(ENOENT):
			fprintf(stderr, "Error: File %s not found\n", file_name);
			code = ERROR_CANNOT_OPEN_FILE;
			goto cleanup;
		case AVERROR(EINVAL):
			fprintf(stderr, "Error: Invalid arguments for opening file %s\n", file_name);
			code = ERROR_ARGUMENTS_INVALID;
			goto cleanup;
		case AVERROR(ENOMEM):
			fprintf(stderr, "Error: Not enough memory to open file %s\n", file_name);
			code = ERROR_NOTENOUGH_MEMORY;
			goto cleanup;
		default:
			fprintf(stderr, "Error: Failed to open file %s\n", file_name);
			code = ERROR_UNKNOWN;
			goto cleanup;
		}
	}
	int streams = avformat_find_stream_info(fcnt, NULL);
	if (streams < 0)
	{
		fprintf(stderr, "Error: failed to retrieve stream info\n");
		code = ERROR_FORMAT_INVALID;
		goto cleanup;
	}
	if (fcnt->streams[0]->codecpar->codec_type != AVMEDIA_TYPE_AUDIO)
	{
		fprintf(stderr, "Error: file %s isn't audio file\n", file_name);
		code = ERROR_FORMAT_INVALID;
		goto cleanup;
	}
	channels = fcnt->streams[0]->codecpar->ch_layout.nb_channels;
	if (channels < max_num)
	{
		fprintf(stderr, "Error: number of channels less %d\n", max_num);
		code = ERROR_FORMAT_INVALID;
		goto cleanup;
	}
	*output_sample_rate = fcnt->streams[0]->codecpar->sample_rate;
	codec = avcodec_find_decoder(fcnt->streams[0]->codecpar->codec_id);
	if (!codec)
	{
		fprintf(stderr, "Error: codec not found\n");
		code = ERROR_FORMAT_INVALID;
		goto cleanup;
	}
	ccnt = avcodec_alloc_context3(codec);
	if (!ccnt)
	{
		fprintf(stderr, "Error: could not allocate audio codec context\n");
		code = ERROR_FORMAT_INVALID;
		goto cleanup;
	}
	if (avcodec_open2(ccnt, codec, NULL) < 0)
	{
		fprintf(stderr, "Error: could not open codec\n");
		code = ERROR_FORMAT_INVALID;
		goto cleanup;
	}
	packet.data = NULL;
	packet.size = 0;
	fr = av_frame_alloc();
	if (!fr)
	{
		fprintf(stderr, "Error: could not allocate AVFrame\n");
		code = ERROR_NOTENOUGH_MEMORY;
		goto cleanup;
	}

	size_t sz = 0;
	size_t capacity = 1024;
	*output_data = (double*)malloc(capacity * sizeof(double));
	if (*output_data == NULL)
	{
		fprintf(stderr, "Error: initial malloc failed\n");
		code = ERROR_NOTENOUGH_MEMORY;
		goto cleanup;
	}

	while (av_read_frame(fcnt, &packet) >= 0)
	{
		int retcodes = avcodec_send_packet(ccnt, &packet);
		if (retcodes < 0)
		{
			switch (-retcodes)
			{
			case AVERROR_EOF:
				fprintf(stderr, "Error: Codec has been flushed and no longer accepts packets\n");
				code = ERROR_UNSUPPORTED;
				goto cleanup;
			case AVERROR(EINVAL):
				fprintf(stderr, "Error: Invalid arguments for opening file %s\n", file_name);
				code = ERROR_ARGUMENTS_INVALID;
				goto cleanup;
			case AVERROR(ENOMEM):
				fprintf(stderr, "Error: Not enough memory to open file %s\n", file_name);
				code = ERROR_NOTENOUGH_MEMORY;
				goto cleanup;
			default:
				fprintf(stderr, "Error: Failed to send packet to codec, reason\n");
				code = ERROR_UNKNOWN;
				goto cleanup;
			}
		}

		while (avcodec_receive_frame(ccnt, fr) == 0)
		{
			size_t num_samples = fr->nb_samples;
			if (sz + num_samples > capacity)
			{
				capacity = (sz + num_samples) * 2;
				double* temp = (double*)realloc(*output_data, capacity * sizeof(double));
				if (temp == NULL)
				{
					fprintf(stderr, "Error: realloc failed\n");
					free(*output_data);
					*output_data = NULL;
					code = ERROR_NOTENOUGH_MEMORY;
					goto cleanup;
				}
				*output_data = temp;
			}

			if (fr->format == AV_SAMPLE_FMT_FLT || fr->format == AV_SAMPLE_FMT_FLTP)
			{
				float* ptr = (float*)fr->extended_data[ch_num - 1];
				for (size_t i = 0; i < num_samples; ++i)
				{
					(*output_data)[sz + i] = (double)ptr[i];
				}
			}
			else
			{
				fprintf(stderr, "Error: unsupported sample format\n");
				code = ERROR_UNSUPPORTED;
				goto cleanup;
			}
			sz += num_samples;
		}
		av_packet_unref(&packet);
	}
	*output_size = sz;
	code = SUCCESS;
	goto cleanup;

cleanup:
	if (fr != NULL)
		av_frame_free(&fr);
	if (ccnt != NULL)
		avcodec_free_context(&ccnt);
	if (fcnt != NULL)
	{
		avformat_close_input(&fcnt);
		avformat_free_context(fcnt);
	}
	return code;
}

int resample_audio(SwrContext** swr_ctx, double** output_data, size_t* output_size, int src_rate, int dst_rate, double* input_data, size_t input_size)
{
	*swr_ctx = swr_alloc();
	if (!*swr_ctx)
	{
		fprintf(stderr, "Error: could not allocate resampling context\n");
		return ERROR_UNSUPPORTED;
	}

	int res, fmt;

	res = av_opt_set_int(*swr_ctx, "in_channel_layout", AV_CH_LAYOUT_STEREO, 0);
	if (res < 0)
	{
		fprintf(stderr, "Error: Failed to set input channel layout\n");
		goto cleans;
	}

	res = av_opt_set_int(*swr_ctx, "out_channel_layout", AV_CH_LAYOUT_STEREO, 0);
	if (res < 0)
	{
		fprintf(stderr, "Error: Failed to set input channel layout\n");
		goto cleans;
	}

	res = av_opt_set_int(*swr_ctx, "in_sample_rate", src_rate, 0);
	if (res < 0)
	{
		fprintf(stderr, "Error: Failed to set input sample rate\n");
		goto cleans;
	}

	res = av_opt_set_int(*swr_ctx, "out_sample_rate", dst_rate, 0);
	if (res < 0)
	{
		fprintf(stderr, "Error: Failed to set input sample rate\n");
		goto cleans;
	}

	fmt = av_opt_set_sample_fmt(*swr_ctx, "in_sample_fmt", AV_SAMPLE_FMT_FLT, 0);
	if (fmt < 0)
	{
		fprintf(stderr, "Error: Failed to set input sample format\n");
		goto cleans;
	}

	fmt = av_opt_set_sample_fmt(*swr_ctx, "out_sample_fmt", AV_SAMPLE_FMT_FLT, 0);
	if (fmt < 0)
	{
		fprintf(stderr, "Error: Failed to set input sample format\n");
		goto cleans;
	}

	if (swr_init(*swr_ctx) < 0)
	{
		fprintf(stderr, "Error: could not initialize resampling context\n");
		return ERROR_UNSUPPORTED;
	}

	size_t dst_nb_samples = (size_t)av_rescale_rnd(swr_get_delay(*swr_ctx, src_rate) + input_size, dst_rate, src_rate, AV_ROUND_UP);

	*output_data = (double*)malloc(dst_nb_samples * sizeof(double));
	if (!*output_data)
	{
		fprintf(stderr, "Error: resample memory allocation failed\n");
		return ERROR_NOTENOUGH_MEMORY;
	}

	float* in = (float*)input_data;
	float* out = (float*)*output_data;

	int num_samples_converted = swr_convert(*swr_ctx, (uint8_t**)&out, (int)dst_nb_samples, (const uint8_t**)&in, (int)input_size);
	if (num_samples_converted < 0)
	{
		fprintf(stderr, "Error: resampling failed\n");
		return ERROR_UNKNOWN;
	}

	*output_size = (size_t)num_samples_converted;
	return SUCCESS;

cleans:
	swr_free(swr_ctx);
	return ERROR_DATA_INVALID;
}
