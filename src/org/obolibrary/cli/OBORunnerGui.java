package org.obolibrary.cli;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.obolibrary.gui.GuiMainFrame;

/**
 * GUI access for converter.
 */
public class OBORunnerGui extends OBORunner {

	public static void main(String[] args) {
		
		Logger rootLogger = Logger.getRootLogger();
		final BlockingQueue<String> logQueue =  new ArrayBlockingQueue<String>(100);
		
		rootLogger.addAppender(new AppenderSkeleton() {
			
			public boolean requiresLayout() {
				return false;
			}
			
			public void close() {
				// do nothing
			}
			
			@Override
			protected void append(LoggingEvent event) {
				String message = event.getRenderedMessage();
				logQueue.add(message);
			}
		});
		
		OBORunnerConfiguration config = OBORunnerConfigCLIReader.readConfig(args);
		if (config.showHelp.getValue()) {
			System.out.println("GUI version of OBORunner. All parameters are set using the GUI.");
			System.exit(0);
		}
		
		final Logger logger = Logger.getLogger(OBORunnerGui.class);

		// Start GUI
		new GuiMainFrame(logQueue, config) {

			// generated
			private static final long serialVersionUID = -7439731894262579201L;

			@Override
			protected void executeConversion(OBORunnerConfiguration config) {
				String buildDir = config.buildDir.getValue();

				try {
					if (buildDir != null) {
						buildAllOboOwlFiles(buildDir, config, logger);
					}
					runConversion(config, logger);
				} catch (Exception e) {
					logger.error("Internal error: "+ e.getMessage(), e);
				} catch (Throwable e) {
					logger.fatal("Internal error: "+ e.getMessage(), e);
				}
			}
		};
	}
}
