package com.sunbeaminfo.dbda.mrhbase;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class BooksMapper extends TableMapper<Text, DoubleWritable> {
	private Text subjectWritable = new Text();
	private DoubleWritable priceWritable = new DoubleWritable();
	@Override
	protected void map(ImmutableBytesWritable key, Result value,
			Mapper<ImmutableBytesWritable, Result, Text, DoubleWritable>.Context context)
					throws IOException, InterruptedException {
		String subject = Util.getSubject(value);
		Double price = Util.getPrice(value);
		if(subject!=null && price!=null) {
			subjectWritable.set(subject);
			priceWritable.set(price);
			context.write(subjectWritable, priceWritable);
		}
	}
}

