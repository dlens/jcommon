#!/usr/bin/perl

# First I need to get only the beginning comment

use strict;
use warnings;
use v5.10;

my $file = shift @ARGV;
my $license_file = "";
if (scalar(@ARGV) >= 1) {
  my $lf = shift @ARGV;
  open(my $lf_h, '<', $lf) or die "Can't read file `$lf` [$!]\n";
  {
          local $/;
          $license_file = <$lf_h>;
  }
  close($lf_h);
}

open(my $fh, '<', $file) or die "Can't read file '$file' [$!]\n";
my $first_line = "";
$_ = <$fh>;
if (/^\/\*/) {
  # Read until we reach a */
  while (<$fh>) {
    if (/\*\//) {
      last;
    }
  }
} else {
  chomp;
  $first_line = $_;
}


if (length($license_file) > 0) {
  say $license_file;
}

if (length($first_line) > 0) {
  say $first_line;
}

while (<$fh>) {
  chomp;
  say;
}

close($fh)
