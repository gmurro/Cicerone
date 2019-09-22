<?php

require_once '../Globetrotter.php';

$response = array();

if($_POST['id'] && $_POST['cittaEsercizio'] && $_POST['descrizioneCicerone']){
    $id = $_POST['id'];
    $citta = $_POST['cittaEsercizio'];
    $descrizione = $_POST['descrizioneCicerone'];

    $globetrotter = new Globetrotter();
    $response = $globetrotter->diventaCicerone($id, $citta, $descrizione);



}else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}

header('Content-Type: application/json');
echo json_encode($response);