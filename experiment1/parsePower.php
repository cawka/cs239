#!/usr/bin/php
<?php

date_default_timezone_set('America/Los_Angeles');

function save( $a )
{
	print strftime('%FT%T',($a[time]/1000))."	$a[CPU]	$a[LCD]	$a[Wifi] $a[threeg]	$a[total]	$a[time]\n";
}

print "#Time	CPU	LCD	Wifi	threeg	total	time_in_ms\n";

$time=0;
$file=fopen( "php://stdin", "r" );
while( !feof($file) )
{
	$line=fgets( $file );
	if( preg_match("/----------(\d+)----------/", $line, $matches) )
	{
		if( isset($power) ) save( $power );
		$power=array();
		$power['time']=$matches[1];
		//print strftime('%FT%T', ($time/1000))."\n";
	}
	elseif( preg_match("/^(\S+) power: ([0-9\.]+)/", $line, $matches) )
	{
		$power[$matches[1]]=$matches[2];
//		print $matches[1]." = ".$matches[2]."\n";
	}
	elseif( preg_match("/Total power consumption is:([0-9\.]+)/", $line, $matches) )
	{
		$power['total']=$matches[1];
	}
}


