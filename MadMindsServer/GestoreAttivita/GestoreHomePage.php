<?php
require_once '../Attivita.php';

$response = array();

$attivita = new Attivita();
$response =$attivita->homePage();
header('Content-Type: application/json');
print json_encode($response);
?>