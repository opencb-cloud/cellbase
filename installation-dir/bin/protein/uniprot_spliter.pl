#!/usr/bin/perl

use strict;

my $cont = 0;
my $chunk = 0;
my $before_entry = 1;
my $in_entry = 0;
my $header = "";

if(@ARGV != 2) {
  print "dasasasddasads\n";
  exit;
}

my $xml_file = $ARGV[0];
my $outdir = $ARGV[1];

print $xml_file."\n";

open(FILE, "$xml_file") || die "dsaaaaaa";
my $line;

while($line = <FILE>) {
 
  if($line =~ /^<entry[\w]*/ ) {
    $in_entry = 1;
    $before_entry = 0;
    if($cont % 10000 == 0) {
      open(OUTFILE, ">$outdir/chunk_$chunk.xml");
      print OUTFILE $header;
    }
    $cont++;
  }

 ## reading header
  if($before_entry == 1) {
    $header .= $line;
  }

  if($in_entry == 1) {
    print OUTFILE $line;
  }

 if($line =~ /^<\/entry>/ ) {
    $in_entry = 0;
    if($cont % 10000 == 0) {
      print OUTFILE "</uniprot>";
      close(OUTFILE, ">$outdir/chunk_$chunk.xml");
      $chunk++;
    }
  }
}

print OUTFILE "</uniprot>";
close(OUTFILE, ">$outdir/chunk_$chunk.xml");

close(FILE);

