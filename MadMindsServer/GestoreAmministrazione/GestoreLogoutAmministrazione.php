<?php
$_SESSION = array();
session_destroy();
header('location: http://www.madminds.tk/VistaAmministrazione/VistaLogin.php');
exit();
?>

