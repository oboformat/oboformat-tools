package org.obolibrary.cli;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JOptionPane;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.obolibrary.gui.GuiMainFrame;

/**
 * GUI access for converter.
 */
public class OBORunnerGui extends OBORunner {

	private final static Logger logger = Logger.getLogger(OBORunnerGui.class);
	
	// SimpleDateFormat is NOT thread safe
	// encapsulate as thread local
	private final static ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>(){

		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	
	public static void main(String[] args) {
		
		Logger rootLogger = Logger.getRootLogger();
		final BlockingQueue<String> logQueue =  new ArrayBlockingQueue<String>(100000);
		
		rootLogger.addAppender(new AppenderSkeleton() {
			
			public boolean requiresLayout() {
				return false;
			}
			
			public void close() {
				// do nothing
			}
			
			@Override
			protected void append(LoggingEvent event) {
				try {
					StringBuilder sb = new StringBuilder();
					sb.append(df.get().format(new Date(event.timeStamp)));
					sb.append(' ');
					sb.append(event.getLevel());
					sb.append(' ');
					sb.append(event.getRenderedMessage());
					logQueue.put(sb.toString());
				} catch (InterruptedException e) {
					logger.fatal("Interruped during wait for writing to the message panel.", e);
				}
			}
		});
		
		OBORunnerConfiguration config = OBORunnerConfigCLIReader.readConfig(args);
		if (config.showHelp.getValue()) {
			System.out.println("GUI version of OBORunner. All parameters are set using the GUI.");
			System.exit(0);
		}
		
		

		// Start GUI
		new GuiMainFrameWorker(logQueue, config);
	}

	private static final class GuiMainFrameWorker extends GuiMainFrame {
		// generated
		private static final long serialVersionUID = -7439731894262579201L;
	
		private GuiMainFrameWorker(BlockingQueue<String> logQueue,
				OBORunnerConfiguration config) {
			super(logQueue, config);
		}
	
		@Override
		protected void executeConversion(final OBORunnerConfiguration config) {
			disableRunButton();
			// execute the conversion in a separate Thread, otherwise the GUI might be blocked.
			Thread t = new Thread() {

				@Override
				public void run() {
					try {
						String buildDir = config.buildDir.getValue();
						if (buildDir != null) {
							buildAllOboOwlFiles(buildDir, config, logger);
						}
						runConversion(config, logger);
						logger.info("Finished release manager process");
						JOptionPane.showMessageDialog(GuiMainFrameWorker.this, "Finished ontology conversion.");
					}catch (Exception e) {
						logger.error("Internal error: "+ e.getMessage(), e);
					} catch (Throwable e) {
						logger.fatal("Internal error: "+ e.getMessage(), e);
					}
					finally {
						enableRunButton();
					}
				}
			};
			t.start();
		}
	}
}
