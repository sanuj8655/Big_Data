package com.sunbeam.ncdc;

import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class AvgTempReducer extends Reducer<IntWritable, IntWritable, IntWritable, DoubleWritable> {
	@Override
	protected void reduce(IntWritable yearWr, Iterable<IntWritable> values, Reducer<IntWritable, IntWritable, IntWritable, DoubleWritable>.Context context)
			throws IOException, InterruptedException {
		Stream<IntWritable> stream = StreamSupport.stream(values.spliterator(), false);
		double avgTemp = stream.mapToInt(tempWr -> tempWr.get()).average().orElse(0.0);
		context.write(yearWr, new DoubleWritable(avgTemp));
	}
}
