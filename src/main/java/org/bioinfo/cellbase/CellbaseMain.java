package org.bioinfo.cellbase;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CellbaseMain {

	protected static Options options;
	protected static CommandLine commandLine;
	protected static CommandLineParser parser;
	
	
	public CellbaseMain() {
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		parser = new PosixParser();
		options = new Options();
		options.addOption(OptionFactory.createOption("file", "outdir ...", true, true));
		options.addOption(OptionFactory.createOption("o", "outdir ...", true, true));	
		try {
			parse(args, false);
			
			if(commandLine.hasOption("file")) {
				System.out.println("tomaaaaaa!!!!!!!!!!");
			}
			
			
			
			
			
			
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void parse(String[] args, boolean stopAtNoOption) throws ParseException, IOException {
		commandLine = parser.parse( options, args, stopAtNoOption);

//		if(commandLine.hasOption("outdir")) {
//			this.outdir = commandLine.getOptionValue("outdir");
//		}
//		if(commandLine.hasOption("log-file")) {
//			logger.addLogFile(new File(commandLine.getOptionValue("log-file")));
//		}
//		if(commandLine.hasOption("log-level")) {
//			logger.setLevel(Integer.parseInt(commandLine.getOptionValue("log-level")));
//		}
//		if(commandLine.hasOption("report")) {
//			this.report = commandLine.getOptionValue("report", "pdf");
//		}
	}
	
}
