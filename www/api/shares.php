<?php
/*
  ##########################################################
  # shares.php
  # script to manage location shares
  # Input Data (POST or GET data):
  #      debug:  If set, returns debug messages
  #      mode:   add | delete | update - what to do
  # Add Mode
  #		   ownerId
  #                viewerId
  # Delete Mode:
  #                shareId
  # list Mode:
  #                userId - lists all shares visible to this user.
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

if (!isset($_REQUEST['mode'])) {
  print "-1";
}
else {
  $mode = $_REQUEST['mode'];
  $msg = $msg . " Requesting mode ".$mode.".<br/>\n";

  switch (strtolower($mode)) {
  case "add":
    $msg = $msg . "found mode ".$mode."<br/>\n";
    $ownerId = getIntParam('ownerId');
    $viewerId = getIntParam('viewerId');
    $msg = $msg. "ownerId=".$ownerId. ", viewerId=".$viewerId
      ."<br/>\n";
    $retVal = addShare($ownerId,$viewerId);
    print $retVal;
    break;
  case 'delete':
    $msg = $msg . "found mode ".$mode."<br/>\n";
    $shareId = getIntParam('shareId');
    $msg = $msg . " shareId=".$shareId."<br/>\n";
    $retVal = delShare($shareId);
    print "<p>retVal=".$retVal."</p>\n";
    break;
  case 'list':
    $msg = $msg . "found mode ".$mode."<br/>\n";
    $viewerId = getIntParam('viewerId');
    $retVal = listShares($viewerId);
    print $retVal;
    break;
  default:    
    $msg = $msg . "Oh no - mode ".$mode." not found..not doing anything..<br/>\n";
  }  
}
if ($debug) echo $msg;

function sqlStr($str) {
  /* Enclose the given string in single quotes to use as a string in SQL 
   * statements
   */
  return ("'".$str."'");
}

function addShare($ownerId,$viewerId) {
  /*
   * add a share to the database.
   */
  global $msg;
  $query = "insert into shares (ownerId, viewerId)"
    . " values "
    . "( ".$ownerId.", " 
    . $viewerId.")"; 
  $msg = $msg . $query;    
  mysql_query($query)
    or die('Query Failed: '. $query.': '.mysql_error());
  return(mysql_insert_id());
}

function delShare($shareId) {
/* 
 * delete share number shareId from the database.
 */
  global $msg;
  $query  = "delete from shares where shareId=".$shareId;
  $msg = $msg."delUser: query=".$query."<br/>\n";
  $result = mysql_query($query) 
    or die('Query failed: ' . mysql_error());
  return(1);
}

function listShares($viewerId) {
  global $msg;
  /* returns a list of all shares available to $viewerId as a JSON array */
  $query = "select shareId,ownerId,viewerId from shares where viewerId=".$viewerId;
  $result = mysql_query($query) 
    or die('Query failed: ' . mysql_error());
  $rows=array();
  while($r = mysql_fetch_assoc($result)) {
    $rows[] = $r;
  }
  $rowJSON = json_encode($rows);
  $msg = $msg . $rowJSON;
  return($rowJSON);
}


?>
