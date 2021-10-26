package com.sunbeaminfo.dbda.mrhbase;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class BooksReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
	@Override
	protected void reduce(Text key, Iterable<DoubleWritable> values,
			Reducer<Text, DoubleWritable, Text, DoubleWritable>.Context context) throws IOException, InterruptedException {
		double total = 0.0;
		for (DoubleWritable price : values) {
			total = total + price.get();
		}
		DoubleWritable totalWritable = new DoubleWritable(total);
		context.write(key, totalWritable);
	}
}
