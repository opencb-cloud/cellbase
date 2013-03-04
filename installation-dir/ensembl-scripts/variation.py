#!/usr/bin/python

import os;
import argparse;

parser = argparse.ArgumentParser(prog="variation");
parser.add_argument("-od", "--out-dir", action="store", dest="outDirectory", help="input directory to save the results");
parser.add_argument("-ch", "--chromosome", action="store", dest="chromosome", help="select the chromosomes if you want to data download");
parser.add_argument("--user", action="store", dest="user", help="user or database");
parser.add_argument("--database", action="store", dest="database", help="name of database");
parser.add_argument("--password", action="store", dest="password", help="password of database");
parser.add_argument("--port", action="store", dest="port", help="port of database");
parser.add_argument("--ip", action="store", dest="ip", help="ip of database");


parser.set_defaults(chromosome=[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22]);
parser.set_defaults(user="root");
parser.set_defaults(outDirectory="/tmp/variation_snp");
parser.set_defaults(database="project");
parser.set_defaults(password="cafetero");
parser.set_defaults(port="3306");
parser.set_defaults(ip="127.0.0.1");
args = parser.parse_args();

outDirectory = args.outDirectory;
user = args.user;
chromosome = args.chromosome;
database = args.database;
password = args.password;
port = args.port;
ip = args.ip;

select_variation = "select v.name, sq.name, vf.seq_region_start, vf.seq_region_end, vf.seq_region_strand, vf.allele_string, v.ancestral_allele, vf.map_weight, vf.validation_status, vf.consequence_types, vf.somatic, vf.minor_allele, vf.minor_allele_freq, vf.minor_allele_count from variation v, variation_feature vf, seq_region sq where v.variation_id=vf.variation_id and vf.seq_region_id=sq.seq_region_id and sq.name=";
select_transcript_variation = "select vf.variation_name, tv.feature_stable_id, tv.allele_string, tv.somatic, tv.consequence_types, tv.cds_start, tv.cds_end, tv.cdna_start,tv.cdna_end,tv.translation_start,tv.translation_end, tv.distance_to_transcript, tv.codon_allele_string, tv.pep_allele_string, tv.hgvs_genomic, tv.hgvs_transcript, tv.hgvs_protein, tv.polyphen_prediction, tv.polyphen_score, tv.sift_prediction, tv.sift_score from transcript_variation tv, variation_feature vf, seq_region sq where tv.variation_feature_id=vf.variation_feature_id and vf.seq_region_id=sq.seq_region_id and sq.name=";
select_phenotype = "select v.name, va. associated_variant_risk_allele, va.risk_allele_freq_in_controls, va.p_value, va.associated_gene,  ph.name, ph. description, sr.name, sr.version,  st.name, st.study_type, st.url, st. description from variation_annotation va, variation v, phenotype ph, study st, source sr, variation_feature vf, seq_region sq where va.variation_id=v.variation_id and va.phenotype_id=ph.phenotype_id and va.study_id=st.study_id and st.source_id=st.source_id and v.variation_id=vf.variation_id and vf.seq_region_id=sq.seq_region_id and sq.name=";
select_xref = "select v.name as snp_id, vs.name as syn_id, sr.name as source, sr.version from variation_synonym vs, variation v, source sr, variation_feature vf, seq_region sq where vs.variation_id=v.variation_id and vs.source_id=sr.source_id and vs.variation_id=vf.variation_id and vf.seq_region_id=sq.seq_region_id and sq.name=";
select_regulatory = "select vf.variation_name, mfv.feature_stable_id, mfv.consequence_types, sq.name, mf.seq_region_start, mf.seq_region_end, mf.seq_region_strand, mf.display_label, mf.score from motif_feature_variation mfv, homo_sapiens_funcgen_70_37.motif_feature mf, variation_feature vf, seq_region sq where mfv.motif_feature_id=mf.motif_feature_id and mfv.variation_feature_id=vf.variation_feature_id and vf.seq_region_id=sq.seq_region_id and sq.name="; 
select_frequency_allele = "select v.name, ac.allele, a.frequency, a.count, s.name as sample from allele a, allele_code ac, variation v, sample s, variation_feature vf, seq_region sq where a.allele_code_id=ac.allele_code_id and a.variation_id=v.variation_id and a.sample_id=s.sample_id and v.variation_id=vf.variation_id and vf.seq_region_id=sq.seq_region_id and sq.name=";
select_frequency_genotype = "select v.name, ac.allele, a.frequency, a.count, s.name as sample from allele a, allele_code ac, variation v, sample s, variation_feature vf, seq_region sq where a.allele_code_id=ac.allele_code_id and a.variation_id=v.variation_id and a.sample_id=s.sample_id and v.variation_id=vf.variation_id and vf.seq_region_id=sq.seq_region_id and sq.name=";


select_test = "select * from monitor where id=";

for chromosomeNumber in chromosome:
	if not os.path.exists(outDirectory):
		os.makedirs(outDirectory);
	if not os.path.exists(os.path.join(outDirectory, "chromosome_" + str(chromosomeNumber))):
                os.makedirs(os.path.join(outDirectory, "chromosome_" + str(chromosomeNumber)));
       	else:
			print("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_variation.txt");
			os.system("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_variation.txt");
			print("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_transcript_variation.txt");
			os.system("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_transcript_variation.txt");
			print("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_phenotype.txt");
			os.system("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_phenotype.txt");
			print("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_xref.txt");
			os.system("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_xref.txt");
			print("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_regulatory.txt");
			os.system("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_regulatory.txt");
			print("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_frequency_allele.txt");
			os.system("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_frequency_allele.txt");
			print("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_frequency_genotype.txt");
			os.system("mysql --user=" + user + " --password=" + password + " --database=" + database + " --host=" + ip + " -e \"" + select_test + str(chromosomeNumber) + "\" > " + outDirectory + "/chromosome_" + str(chromosomeNumber) + "/select_frequency_genotype.txt");
