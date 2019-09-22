<?php

require_once '../Cicerone.php';

$response = array();



if($_POST['idCicerone']){

    $id = $_POST['idCicerone'];
    $cicerone = new Cicerone();
    $response = $cicerone-> visualizzaProfiloCompleto($id);

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}


echo json_encode($response);

?>