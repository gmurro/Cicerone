<?php

require_once '../Recensione.php';

$response = array();



if($_POST['idCicerone']){

    $id = $_POST['idCicerone'];
    $cicerone = new Recensione();
    $response = $cicerone-> visualizzaRecensioniCicerone($id);

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}


echo json_encode($response);

?>