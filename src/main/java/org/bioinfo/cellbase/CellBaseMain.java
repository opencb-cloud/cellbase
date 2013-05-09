package org.bioinfo.cellbase;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.bioinfo.cellbase.parser.GeneParser;
import org.bioinfo.cellbase.parser.GenomeSequenceFastaParser;
import org.bioinfo.formats.exception.FileFormatException;

public class CellBaseMain {

	private static Options options;
	private static CommandLine commandLine;
	private static CommandLineParser parser;
	
	private Logger logger;
	
	static {
		parser = new PosixParser();
	}
	
	public CellBaseMain() {
		initOptions();
	}
	
	private static void initOptions() {
		options = new Options();
		options.addOption(OptionFactory.createOption("build", "Build values: core, genome_sequence, variation, protein"));
		options.addOption(OptionFactory.createOption("indir", "i",  "Input directory with data files", false));
		options.addOption(OptionFactory.createOption("outdir", "o",  "Output directory to save the JSON result", false));
		options.addOption(OptionFactory.createOption("outfile", "Output directory to save the JSON result", false));
		
		// Core options
		options.addOption(OptionFactory.createOption("gtf-file", "Output directory to save the JSON result", false));
		options.addOption(OptionFactory.createOption("gene-description", "Output directory to save the JSON result", false));
		options.addOption(OptionFactory.createOption("xref-file", "Output directory to save the JSON result", false));
		options.addOption(OptionFactory.createOption("tfbs-file", "Output directory to save the JSON result", false));
		options.addOption(OptionFactory.createOption("mirna-file", "Output directory to save the JSON result", false));
		options.addOption(OptionFactory.createOption("genome-sequence-dir", "Output directory to save the JSON result", false));
		
		options.addOption(OptionFactory.createOption("species", "s",  "Sapecies...", false, true));
		
		options.addOption(OptionFactory.createOption("log-level", "DEBUG -1, INFO -2, WARNING - 3, ERROR - 4, FATAL - 5", false));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initOptions();
		try {
			parse(args, false);
			String buildOption = null;
			
			// no needed to check as 'build' arg is required in Options
			if(!commandLine.hasOption("build") || commandLine.getOptionValue("build").equals("")) {
				
			}
			
			buildOption = commandLine.getOptionValue("build");
			
			if(buildOption.equals("genome-sequence")) {
				System.out.println("In genome-sequence");
				String indir = commandLine.getOptionValue("indir");
				String outfile = commandLine.getOptionValue("outfile", "/tmp/genome_seq.json");
				if(indir != null) {
					GenomeSequenceFastaParser genomeSequenceFastaParser = new GenomeSequenceFastaParser();
					genomeSequenceFastaParser.parseFastaGzipFilesToJson(new File(indir), new File(outfile));
				}
			}
			
			if(buildOption.equals("core")) {
				System.out.println("In core");
				String gtfFile = commandLine.getOptionValue("gtf-file");
				String geneDescriptionFile = commandLine.getOptionValue("gene-description", "");
				String xrefFile = commandLine.getOptionValue("xref-file", "");
				String tfbsFile = commandLine.getOptionValue("tfbs-file", "");
				String mirnaFile = commandLine.getOptionValue("mirna-file", "");
				String genomeSequenceDir = commandLine.getOptionValue("genome-sequence-dir", "");
				String outfile = commandLine.getOptionValue("outfile", "/tmp/gene.json");
				if(gtfFile != null) {
					try {
						GeneParser geneParser = new GeneParser();
						geneParser.parseToJson(new File(gtfFile), new File(geneDescriptionFile), new File(xrefFile), new File(tfbsFile), new File(mirnaFile), new File(genomeSequenceDir),  new File(outfile));
					} catch (SecurityException | NoSuchMethodException | FileFormatException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void parse(String[] args, boolean stopAtNoOption) throws ParseException, IOException {
		parser = new PosixParser();
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
