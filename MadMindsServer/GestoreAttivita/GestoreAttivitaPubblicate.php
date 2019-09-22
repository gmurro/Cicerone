<?php
require_once '../Attivita.php';

$response = array();
if($_POST['codCicerone']){
    $codCicerone = $_POST['codCicerone'];

    $attivita = new Attivita();
    $response = $attivita->attivitaPubblicate($codCicerone);

} else {
    $response['message'] = "Parametri insufficenti";
}
header('Content-Type: application/json');
echo json_encode($response);