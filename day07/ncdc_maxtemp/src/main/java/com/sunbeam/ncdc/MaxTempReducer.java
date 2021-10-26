package com.sunbeam.ncdc;

import java.io.IOException;
import java.util.OptionalInt;
import java.util.stream.StreamSupport;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class MaxTempReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
	@Override
	protected void reduce(IntWritable monthWr, Iterable<IntWritable> values, Reducer<IntWritable, IntWritable, IntWritable, IntWritable>.Context context)
			throws IOException, InterruptedException {
		OptionalInt maxTempOpt = StreamSupport.stream(values.spliterator(), false)
			.mapToInt(tempWr -> tempWr.get())
			.max();
		if(maxTempOpt.isPresent())
			context.write(monthWr, new IntWritable(maxTempOpt.getAsInt()));
	}
}


