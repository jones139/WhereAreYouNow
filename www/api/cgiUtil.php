<?php
/*
 * A few utility functions to help with retrieving CGI parameters.
 */

function getIntParam($paramStr) {
 if (isset($_REQUEST[$paramStr]))
    $intParam = intVal($_REQUEST[$paramStr]);
 else
   $intParam = NULL;
 return $intParam;
}

function getFloatParam($paramStr) {
 if (isset($_REQUEST[$paramStr]))
    $floatParam = floatVal($_REQUEST[$paramStr]);
 else
   $floatParam = NULL;
 return $floatParam;
}

function getStrParam($paramStr) {
 if (isset($_REQUEST[$paramStr]))
    $strParam = $_REQUEST[$paramStr];
 else
   $strParam = NULL;
 return $strParam;
}

?>