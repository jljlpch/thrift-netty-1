/**
 * 
 */
package cn.trace.thrift.netty.examples;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TProtocol;

import com.facebook.nifty.processor.NiftyProcessor;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftEventHandler;
import com.facebook.swift.service.ThriftServiceProcessor;
import com.google.common.collect.ImmutableList;

import cn.trace.thrift.netty.ThriftNettyServer;
import cn.trace.thrift.netty.configure.ThriftNettyServerDefBuilder;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;

/**
 * @author trace
 *
 */
public class EchoServer {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		ResourceLeakDetector.setLevel(Level.ADVANCED); // check memory leak
		EchoImpl echoImpl = new EchoImpl();
		final NiftyProcessor niftyProcessor = new ThriftServiceProcessor(new ThriftCodecManager(),
				ImmutableList.<ThriftEventHandler> of(), echoImpl);
		TProcessor processor = new TProcessor() {

			@Override
			public boolean process(TProtocol in, TProtocol out) throws TException {

				try {
					return niftyProcessor.process(in, out, null).get();
				} catch (Exception e) {
					throw new TException(e);
				}
			}

		};
		ThriftNettyServer server = new ThriftNettyServer(
				new ThriftNettyServerDefBuilder().processorFactory(new TProcessorFactory(processor)).build());
		server.start();
	}

}
