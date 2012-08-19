<?php 
//////////////////////////////////////////////////////////////
// Setup the databases required by this web interface
//////////////////////////////////////////////////////////////

include("dbconn.php");
include("APIConfig.php");



//////////////////////////////////////////////
// Users
//////////////////////////////////////////////
$sql = "drop table if exists users";
mysql_query($sql,$dbconn)
    or die('Could not connect: ' . mysql_error());

$sql = <<<'EOD'
create table users (
       	     	   userId int not null auto_increment,
                   name varchar(256),
		   userName varchar(256),
		   password varchar(256),
		   role int,
		   icon blob,
		   homeLat float,
		   homeLon float,
		   primary key (userId)
);
EOD;
mysql_query($sql,$dbconn)
    or die('Could not connect: ' . mysql_error());

///////////////////////////////////////////////
// Shares (who can see what)
///////////////////////////////////////////////
$sql = "drop table if exists shares";
mysql_query($sql,$dbconn)
    or die('Could not connect: ' . mysql_error());

$sql = <<<'EOD'
create table shares (
       	     	   shareId int not null auto_increment,
		   ownerId int,
		   viewerId int,
		   primary key (shareId)
);
EOD;
mysql_query($sql,$dbconn)
    or die('Could not connect: ' . mysql_error());

///////////////////////////////////////////////
// Location Points
///////////////////////////////////////////////
$sql = "drop table if exists locPts";
mysql_query($sql,$dbconn)
    or die('Could not connect: ' . mysql_error());

$sql = <<<'EOD'
create table locPts (
       	     	   ptId int not null auto_increment,
 		   userId int,
		   lat float,
		   lon float,
		   date datetime,
		   primary key (ptId)
);
EOD;
mysql_query($sql,$dbconn)
    or die('Could not connect: ' . mysql_error());


?>
