API README
==========

Manipulation of Location Points
===============================

Add a Point
.../points.php?mode=add&userId=1&lat=54&lon=-1&dateStr=2012-01-01%2009:00:00

Delete a point
.../points.php?mode=delete&ptId=1

Get Most Recent Point
.../points.php?mode=getLatest&userId=1

Get History of a User
.../points.php?mode=getHistory&userId=1
.../points.php?mode=getHistory&userId=1&limit=2
.../points.php?mode=getHistory&userId=1&startDate=2012-08-01 00:00:00

Add '&debug' to the end of the URL to get some debugging info if it is
causing trouble!
