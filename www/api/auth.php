<?php
/*******************************************************************
 * WARNING:  THIS AUTHENTICATION SYSTEM IS VERY CRUDE DON'T COPY IT
 *           FOR SOMETHING IMPORTANT!!!!!
 *******************************************************************/
/* Basic Authentication check */
include("APIConfig.php");
include("dbconn.php");
include("cgiUtil.php");
session_start();

/* returns true if user authenticates successfully using POST data
 * by providing a valid username as txtUserId and password as txtPassword.
 * returns false otherwise.
 */
function isLoggedIn() {
  if (!isset($_SESSION['basic_is_logged_in']) 
      || $_SESSION['basic_is_logged_in'] !== true) {
    print "cookie not set...";
    if (isset($_POST['txtUserId']) && isset($_POST['txtPassword'])) {
      print "we have a user name and password...";
      // check if the user id and password combination is correct
      if ($_POST['txtUserId'] === 'graham' && $_POST['txtPassword'] === '1234') {
	// the user id and password match, 
	// set the session
	$_SESSION['basic_is_logged_in'] = true;
	return true;
      } else {
	print "username or password incorrect";
	return false;
      }
    }
  } else {
    return true;
  }   
}



/* logout by calling auth.php?logout */
if (isset($_REQUEST['logout'])) {
  if (isset($_SESSION['basic_is_logged_in'])) {
    unset($_SESSION['basic_is_logged_in']);
  }
}




if (!isLoggedIn()) {
  echo '<h1>Login Please</h1>';
  echo '<form action="auth.php" method="post">';
  echo '<label>User Name:</label><input name="txtUserId"><br/>';
  echo '<label>Password: </label><input name="txtPassword"><br/>';
  echo '<input type="submit">';
  echo '</form>';

} else {
  echo '<h1>Welcome</h1>';
}
?>