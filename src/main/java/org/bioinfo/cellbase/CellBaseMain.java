package org.bioinfo.cellbase;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.bioinfo.cellbase.parser.VariationParser;

public class CellBaseMain {

	private static Options options;
	private static CommandLine commandLine;
	private static CommandLineParser parser;
	
	private Logger logger;
	
	public CellBaseMain() {
		initOptions();
	}
	
	private void initOptions() {
		parser = new PosixParser();
		options = new Options();
		options.addOption(OptionFactory.createOption("build", "Build values: core, genome_sequence, variation, protein"));
		options.addOption(OptionFactory.createOption("indir", "i",  "Input directory with data files"));
		options.addOption(OptionFactory.createOption("outdir", "o",  "Output directory to save the JSON result"));
		
		options.addOption(OptionFactory.createOption("species", "s",  "Sapecies...", false, true));
		
		options.addOption(OptionFactory.createOption("log-level", "DEBUG -1, INFO -2, WARNING - 3, ERROR - 4, FATAL - 5", false));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			parse(args, false);
			String buildOption = null;
			
			// no needed check as 'build' is required in Options
			if(!commandLine.hasOption("build") || commandLine.getOptionValue("build").equals("")) {
				
			}
			
			buildOption = commandLine.getOptionValue("build");
			if(buildOption.equals("core")) {
				
			}
			
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void parse(String[] args, boolean stopAtNoOption) throws ParseException, IOException {
		commandLine = parser.parse(options, args, stopAtNoOption);

//		if(commandLine.hasOption("outdir")) {
//			this.outdir = commandLine.getOptionValue("outdir");
//		}
//		if(commandLine.hasOption("log-level")) {
//			logger.setLevel(Integer.parseInt(commandLine.getOptionValue("log-level")));
//		}
		
		if(args.length > 0 && "variation".equals(args[1])) {
			System.out.println("variation SQL test");
		}
	}
	
}
