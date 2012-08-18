<?php
/*
##########################################################
# points.php
# script to manage location record points (locPts table)
# Input Data (POST or GET data):
#      debug:  If set, returns debug messages
  #      mode:   add | delete | getLatest | getHistory - what to do
  # Add Mode:
  #		   userId int - user id number for the point
    #		   lat float,
    #		   lon float,
    #		   date timestamp, (YYYY-MM-DD HH:MM:SS) 
    #      
    
##########################################################
*/
include("APIConfig.php");
include("dbconn.php");

if (isset($_REQUEST['debug'])) 
  $debug=true; 
else
  $debug=false;
#$debug=true;

$msg="Debug Messages...";

if (isset($_REQUEST['mode'])) {
  $mode = $_REQUEST['mode'];
  $msg = $msg . " Requesting mode ".$mode.".<br/>\n";
}
else {
  $mode = "getLatest";
  $msg = $msg . " mode not specified - using default of ".$mode.".<br/>\n";
}

switch (strtolower($mode)) {
case "add":
  $msg = $msg . "found mode ".$mode."<br/>\n";
  $userId = getIntParam('userId');
  $lat = getFloatParam('lat');
  $lon = getFloatParam('lon');
  $dateStr = getStrParam('dateStr');
  $msg = $msg. "userId=".$userId.", loc=(".$lon.",".$lat."), "
    ."date=".$dateStr.".<br/>\n";
  addPoint($dbconn,$userId,$lat,$lon,$dateStr,$msg);
  break;
case 'delete':
  $msg = $msg . "found mode ".$mode."<br/>\n";
  break;
case 'getlatest':
  $msg = $msg . "found mode ".$mode."<br/>\n";
  $userId = getIntParam('userId');
  $msg = $msg . "userId=".$userId."<br/>\n";
  $retVal = getLatest($userId);
  break;
case 'gethistory':
  $msg = $msg . "found mode ".$mode."<br/>\n";
  break;
case 'default':    
  $msg = $msg . "Oh no - mode ".$mode." not found..not doing anything..<br/>\n";
}  

if ($debug) echo $msg;



function addPoint($dbconn,$userId,$lat,$lon,$dateStr,$msg) {
  $query = "insert into locPts (userId, lat, lon, date)"
	. " values "
	. "( ".$userId.", " 
	. $lat.", ".$lon.", "
	. "'".$dateStr."'" 
	.");";
  $msg = $msg . $query;    
  mysql_query($query)
    or die('Could not connect: ' .mysql_error());
}

function getLatest($userId) {
  $query  = "select ptId,lat,lon,date from locPts where userId=".$userId." order by ptId desc limit 1";
  $result = mysql_query($query) 
    or die('Query failed: ' . mysql_error());
  $rows=array();
  while($r = mysql_fetch_assoc($result)) {
    $rows[] = $r;
  }
  print json_encode($rows);  

}


function getIntParam($paramStr) {
 if (isset($_REQUEST[$paramStr]))
    $intParam = intVal($_REQUEST[$paramStr]);
 else
   $intParam = -1;
 return $intParam;
}

function getFloatParam($paramStr) {
 if (isset($_REQUEST[$paramStr]))
    $floatParam = floatVal($_REQUEST[$paramStr]);
 else
   $floatParam = -1;
 return $floatParam;
}

function getStrParam($paramStr) {
 if (isset($_REQUEST[$paramStr]))
    $strParam = $_REQUEST[$paramStr];
 else
   $strParam = -1;
 return $strParam;
}


?>
