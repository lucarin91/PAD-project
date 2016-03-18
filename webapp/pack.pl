@files = <*>;

foreach $file (@files){
    if ($file ne __FILE__){
	print "packing $file ...\n";
    	`zip -r ../MonitorWebApp-v0.1-${file}.zip $file`;
    }
}
