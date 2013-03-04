#!/usr/bin/python

import os
import argparse

hsapiensChromosomes=['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', 'X', 'Y', 'MT'];

parser = argparse.ArgumentParser(prog="variation");
parser.add_argument("-o", "--outdir", action="store", dest="outDirectory", help="input directory to save the results");
parser.add_argument("-c", "--chromosome", action="store", dest="chromosome", help="select the chromosomes if you want to data download");
parser.add_argument('-v', action='store_true', default=False, dest='verbose', help='Verbose')
parser.add_argument("--host", action="store", dest="host", help="database host");
parser.add_argument("-u", "--user", action="store", dest="user", help="user or database");
parser.add_argument("--database", action="store", dest="database", help="name of database");
parser.add_argument("-p", "--password", action="store", dest="password", help="password of database");
parser.add_argument("-P", "--port", action="store", dest="port", help="port of database");
parser.add_argument("--ip", action="store", dest="ip", help="ip of database");


parser.set_defaults(chromosome=hsapiensChromosomes);
parser.set_defaults(verbose = False);
parser.set_defaults(host="localhost");
parser.set_defaults(user="anonymous");
parser.set_defaults(outDirectory="/tmp/variation_snp");
parser.set_defaults(database="homo_sapiens_variation_70_37");
parser.set_defaults(password="");
parser.set_defaults(port="3306");
parser.set_defaults(ip="127.0.0.1");

args = parser.parse_args();

outDirectory = args.outDirectory;
verbose = args.verbose;
host = args.host;
user = args.user;
chromosome = args.chromosome;
database = args.database;
password = args.password;
port = args.port;
ip = args.ip;

#chromosome=['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', 'X', 'Y', 'MT'];
#user="anonymous";
#outDirectory="/home/ensembl/cellbase_v3/variation";
#database="homo_sapiens_variation_70_37";
#password="";
#port="3306";
#ip="127.0.0.1";

mysql_command_line = "mysql -u " + user + " -h " + host + " -P " + port + " --database=" + database + " -e \""; #no password

variation = "select v.name, sq.name, vf.seq_region_start, vf.seq_region_end, vf.seq_region_strand, vf.allele_string, v.ancestral_allele, vf.map_weight, vf.validation_status, vf.consequence_types, vf.somatic, vf.minor_allele, vf.minor_allele_freq, vf.minor_allele_count from variation v, variation_feature vf, seq_region sq where v.variation_id=vf.variation_id and vf.seq_region_id=sq.seq_region_id and sq.name=";
transcript_variation = "select vf.variation_name, tv.feature_stable_id, tv.allele_string, tv.somatic, tv.consequence_types, tv.cds_start, tv.cds_end, tv.cdna_start,tv.cdna_end,tv.translation_start,tv.translation_end, tv.distance_to_transcript, tv.codon_allele_string, tv.pep_allele_string, tv.hgvs_genomic, tv.hgvs_transcript, tv.hgvs_protein, tv.polyphen_prediction, tv.polyphen_score, tv.sift_prediction, tv.sift_score from transcript_variation tv, variation_feature vf, seq_region sq where tv.variation_feature_id=vf.variation_feature_id and vf.seq_region_id=sq.seq_region_id and sq.name=";
phenotype = "select v.name, va.associated_variant_risk_allele, va.risk_allele_freq_in_controls, va.p_value, va.associated_gene,  ph.name, ph.description, sr.name, sr.version,  st.name, st.study_type, st.url, st.description from variation_annotation va, variation v, phenotype ph, study st, source sr, variation_feature vf, seq_region sq where va.variation_id=v.variation_id and va.phenotype_id=ph.phenotype_id and va.study_id=st.study_id and st.source_id=st.source_id and v.variation_id=vf.variation_id and vf.seq_region_id=sq.seq_region_id and sq.name=";
xref = "select v.name as snp_id, vs.name as syn_id, sr.name as source, sr.version from variation_synonym vs, variation v, source sr, variation_feature vf, seq_region sq where vs.variation_id=v.variation_id and vs.source_id=sr.source_id and vs.variation_id=vf.variation_id and vf.seq_region_id=sq.seq_region_id and sq.name=";
regulatory = "select vf.variation_name, mfv.feature_stable_id, mfv.consequence_types, sq.name, mf.seq_region_start, mf.seq_region_end, mf.seq_region_strand, mf.display_label, mf.score from motif_feature_variation mfv, homo_sapiens_funcgen_70_37.motif_feature mf, variation_feature vf, seq_region sq where mfv.motif_feature_id=mf.motif_feature_id and mfv.variation_feature_id=vf.variation_feature_id and vf.seq_region_id=sq.seq_region_id and sq.name="; 
frequency_allele = "select v.name, ac.allele, a.frequency, a.count, s.name as sample from allele a, allele_code ac, variation v, sample s, variation_feature vf, seq_region sq where a.allele_code_id=ac.allele_code_id and a.variation_id=v.variation_id and a.sample_id=s.sample_id and v.variation_id=vf.variation_id and vf.seq_region_id=sq.seq_region_id and sq.name=";
frequency_genotype = "select v.name, ac1.allele as allele1, ac2.allele as allele2, pg.frequency, pg.count, s.name from population_genotype pg, genotype_code gc1, genotype_code gc2, allele_code ac1, allele_code ac2, variation v, variation_feature vf, seq_region sq, sample s where v.variation_id=pg.variation_id and pg.genotype_code_id=gc1.genotype_code_id and gc1.allele_code_id=ac1.allele_code_id and gc1.haplotype_id=1 and pg.genotype_code_id=gc2.genotype_code_id and gc2.allele_code_id=ac2.allele_code_id and gc2.haplotype_id=2 and pg.sample_id=s.sample_id and v.variation_id=vf.variation_id and vf.seq_region_id=sq.seq_region_id and sq.name=";

select_test = "select * from table where id=";

for chromosomeNumber in chromosome:
	if not os.path.exists(outDirectory):
		os.makedirs(outDirectory);
	if not os.path.exists(os.path.join(outDirectory, "chromosome_" + str(chromosomeNumber))):
		os.makedirs(os.path.join(outDirectory, "chromosome_" + str(chromosomeNumber)));
	else:
		chromClause = "'" + chromosomeNumber + "'"
		outDir = outDirectory + "/chromosome_" + str(chromosomeNumber)
		
		if verbose:
			print(mysql_command_line + variation + chromClause + "\" | gzip > " + outDir + "/variation.txt");
		os.system(mysql_command_line + select_variation + chromClause + "\" | gzip > " + outDir + "/variation.txt");
		
		if verbose:
			print(mysql_command_line + transcript_variation + chromClause + "\" | gzip > " + outDir + "/transcript_variation.txt");
		os.system(mysql_command_line + transcript_variation + chromClause + "\" | gzip > " + outDir + "/transcript_variation.txt");
		
		if verbose:	
			print(mysql_command_line + phenotype + chromClause + "\" | gzip > " + outDir + "/phenotype.txt");
		os.system(mysql_command_line + phenotype + chromClause + "\" | gzip > " + outDir + "/phenotype.txt");
		
		if verbose:
			print(mysql_command_line + xref + chromClause + "\" | gzip > " + outDir + "/xrefs.txt");
		os.system(mysql_command_line + xref + chromClause + "\" | gzip > " + outDir + "/xrefs.txt");
		
		if verbose:
			print(mysql_command_line + regulatory + chromClause + "\" | gzip > " + outDir + "/regulatory.txt");
		os.system(mysql_command_line + regulatory + chromClause + "\" | gzip > " + outDir + "/regulatory.txt");
		
		if verbose:
			print(mysql_command_line + frequency_allele + chromClause + "\" | gzip > " + outDir + "/frequency_allele.txt");
		os.system(mysql_command_line + frequency_allele + chromClause + "\" | gzip > " + outDir + "/frequency_allele.txt");
		
		if verbose:
			print(mysql_command_line + frequency_genotype + chromClause + "\" | gzip > " + outDir + "/frequency_genotype.txt");
		os.system(mysql_command_line + frequency_genotype + chromClause + "\" | gzip > " + outDir + "/frequency_genotype.txt");

