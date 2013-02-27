package org.bioinfo.cellbase;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.bioinfo.cellbase.parser.VariationParser;

public class CellBaseMain {

	protected static Options options;
	protected static CommandLine commandLine;
	protected static CommandLineParser parser;
	
	
	public CellBaseMain() {
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		parser = new PosixParser();
		options = new Options();
		VariationParser vp = new VariationParser();

		vp.parseGvfToJson(new File(
				"/home/echirivella/Downloads/Homo_sapiens.gvf"), new File(
				"/home/echirivella/Downloads/Homo_sapiens.Json"));
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
		
		if(args.length > 0 && "variation".equals(args[1])) {
			System.out.println("variation SQL test");
		}
	}
	
}
