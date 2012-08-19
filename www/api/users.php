<?php
/*
  ##########################################################
  # users.php
  # script to manage users
  # Input Data (POST or GET data):
  #      debug:  If set, returns debug messages
  #      mode:   add | delete | update - what to do
  # Add Mode:
  #		   userName
  #                password
  #                displayName (displayed name)
  #                homeLat,homeLon (location of their home)
  #                icon
  # Delete Mode:
  #                userId
  # Update Mode:
  #                userId
  #                other parameters as for Add.  Only those included are updated.
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
    $userName = getStrParam('userName');
    $password = getStrParam('password');
    $displayName = getStrParam('displayName');
    $homeLat = getFloatParam('homeLat');
    $homeLon = getFloatParam('homeLon');
    $msg = $msg. "userName=".$userName. ", password=".$password.", displayName=".$displayName.", home_loc=(".$homeLon.",".$homeLat."), "
      ."<br/>\n";
    /* Check whether requested user name already exists */
    if (getUserId($userName)==-1)
      $retVal = addUser($userName,$password,$displayName,$homeLon,$homeLat);
    else
      $retVal = -1;
    print $retVal;
    break;
  case 'delete':
    $msg = $msg . "found mode ".$mode."<br/>\n";
    $userId = getIntParam('userId');
    $msg = $msg . " userId=".$userId."<br/>\n";
    $retVal = delUser($userId);
    #print "<p>retVal=".$retVal."</p>\n";
    break;
  case 'getuserid':
    $msg = $msg . "found mode ".$mode."<br/>\n";
    $userName = getStrParam('userName');
    $retVal = getUserId($userName);
    print "<p>retVal=".$retVal."</p>\n";
    break;
  case 'list':
    $msg = $msg . "found mode ".$mode."<br/>\n";
    $retVal = listUsers();
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

function addUser($userName,$password,$displayName,$homeLon,$homeLat) {
/*
 * add a user to the database.
 */
  global $msg;
  $query = "insert into users (userName, password, name, homeLat, homeLon)"
    . " values "
    . "( ".sqlStr($userName).", " 
    . sqlStr($password).", " 
    . sqlStr($displayName).", " 
	. $homeLat.", ".$homeLon
	.");";
  $msg = $msg . $query;    
  mysql_query($query)
    or die('Query Failed: '. $query.': '.mysql_error());
  return(mysql_insert_id());
}

function delUser($userId) {
/* 
 * delete user number userId from the database.
 */
  global $msg;
  $query  = "delete from users where userId=".$userId;
  $msg = $msg."delUser: query=".$query."<br/>\n";
  $result = mysql_query($query) 
    or die('Query failed: ' . mysql_error());
  return(1);
}

function listUsers() {
  global $msg;
  /* returns a list of all users as a JSON array */
  $query = "select userId,name,homeLon,homeLat from users";
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


function getUserId($userName) {
  global $msg;
  /* returns the user id of the given user name, or -1 if it does not exist */
  $query = "select userId from users where userName=".sqlStr($userName)
    ." limit 1";
  $result = mysql_query($query) 
    or die('Query failed: ' . mysql_error());
  $row = mysql_fetch_assoc($result);
  $msg = $msg . json_encode($row);
  if ($row==NULL)
    $userId = -1;
  else
    $userId =  $row['userId'];
  return($userId);
}


?>
