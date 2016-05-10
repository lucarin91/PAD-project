use warnings;
use strict;

my $NAME  = 'MonitorWebApp';
my $PATH  = "build/$NAME";
my $V     = 'v0.3';
my @files = <"$PATH/*">;

foreach my $file (@files) {

    # if ($file ne __FILE__){
    $file =~ s#.*/##;
    my $name = "$NAME-$V-$file";
    print "packing $name..\n";
    `mv $PATH/$file $PATH/$name`;
    `cd $PATH && zip -r $name.zip $name`;
    `rm -rvf $PATH/$name`;

    # }
}

`mv $PATH/*.zip ../build`;
`rm -rvf ./build`;
print "all done!\n";
