<?php
require_once '../Cicerone.php';

$response = array();

if($_POST['codCicerone']){
    $id = $_POST['codCicerone'];

    $cicerone = new Cicerone();

    $response = $cicerone->eliminaProfiloCicerone($id);
    

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}

header('Content-Type: application/json');
echo json_encode($response);