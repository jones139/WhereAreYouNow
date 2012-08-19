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
include("cgiUtil.php");

if (isset($_REQUEST['debug'])) 
  $debug=true; 
else
  $debug=false;
#$debug=true;

$msg="\n<br/>Debug Messages...";

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
  $retVal = addPoint($userId,$lat,$lon,$dateStr);
  print $retVal;
  break;
case 'delete':
  $msg = $msg . "found mode ".$mode."<br/>\n";
  $ptId = getIntParam('ptId');
  $msg = $msg . " ptId=".$ptId."<br/>\n";
  $retVal = delPoint($ptId);
  #print "<p>retVal=".$retVal."</p>\n";
  break;
case 'getlatest':
  $msg = $msg . "found mode ".$mode."<br/>\n";
  $userId = getIntParam('userId');
  $msg = $msg . "userId=".$userId."<br/>\n";
  $retVal = getLatest($userId);
  print $retVal;
  break;
case 'gethistory':
  $msg = $msg . "found mode ".$mode."<br/>\n";
  $userId = getIntParam('userId');
  $limit = getIntParam('limit');
  $startDate = getStrParam('startDate');
  $endDate   = getStrParam('endDate');
  $retVal = getHistory($userId,$limit,$startDate,$endDate);
  print $retVal;
  break;
default:    
  $msg = $msg . "Oh no - mode ".$mode." not found..not doing anything..<br/>\n";
}  

if ($debug) echo $msg;


function addPoint($userId,$lat,$lon,$dateStr) {
/*
 * Add a point (lat,lon) to the database for the given user (userId), with
 * the specified date (dateStr - yyyy-mm-dd hh:mm:ss).
 */
  global $msg;
  $query = "insert into locPts (userId, lat, lon, date)"
	. " values "
	. "( ".$userId.", " 
	. $lat.", ".$lon.", "
	. "'".$dateStr."'" 
	.");";
  $msg = $msg . $query;    
  mysql_query($query)
    or die('Query Failed: '. $query.': '.mysql_error());
  return(mysql_insert_id());
}

function delPoint($ptId) {
/* 
 * delete point number ptId from the database.
 */
  global $msg;
  $query  = "delete from locPts where ptId=".$ptId;
  $msg = $msg."delPoint: query=".$query."<br/>\n";
  $result = mysql_query($query) 
    or die('Query failed: ' . mysql_error());
  return(1);
}


function getLatest($userId) {
/* 
 * Retrieve the latest location point for the specified user (returned as
 * a JSON string.
 */
  global $msg;
  $result = getHistory($userId,1);
  return $result;
}

function getHistory($userId,$limit=NULL,$startDate=NULL,$endDate=NULL) {
/* 
 * Retrieve the location historyfor the specified user (returned as
 * a JSON array of point objects.
 * by default will return all the values in the databse. $limit will limit
 * the number of results to the $limit most recent points.
 * $startDate and $endDate will limit the returned values to a specified date
 * range - format of dates is "yyyy-mm-dd hh:mm:ss".
 */
  global $msg;
  $query  = "select userId,ptId,lat,lon,date from locPts where userId=".$userId;
  if ($startDate)
    $query = $query." and date >= '".$startDate."'";
  if ($endDate)
    $query = $query." and date <= '".$endDate."'";;

  $query = $query." order by date desc";
  if ($limit)
    $query = $query." limit ".$limit;

  $msg = $msg."query=".$query."<br/>\n";
  $result = mysql_query($query);
    if ($result) {
      $rows=array();
      while($r = mysql_fetch_assoc($result)) {
	$rows[] = $r;
      }
      return (json_encode($rows));
    } else {
      $msg = $msg . 'Query failed:'. mysql_error().'<br/>\n';
      return(-1);
    }

}


?>
