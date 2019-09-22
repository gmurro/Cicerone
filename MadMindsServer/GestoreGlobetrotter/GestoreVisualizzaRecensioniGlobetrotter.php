<?php

require_once '../Recensione.php';

$response = array();



if($_POST['idGlobetrotter']){

    $id = $_POST['idGlobetrotter'];
    $globetrotter = new Recensione();
    $response = $globetrotter-> visualizzaRecensioniGlobetrotter($id);

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}


echo json_encode($response);

?>