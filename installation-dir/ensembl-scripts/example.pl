#!/usr/bin/perl

use JSON;

my %data = (var1 => 'val1',
	    var2 => 'val2',
	    var3 => 'val3',
	    var4 => 'val4');

my @chrom = (data => %data,
	     data2 => %data,
	     nada => 'esto vale esto');

my $json_text = to_json(\@chrom);
print $json_text;
