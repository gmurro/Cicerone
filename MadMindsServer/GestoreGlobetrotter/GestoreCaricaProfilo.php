<?php

require_once '../Globetrotter.php';

$response = array();

if($_POST['idGlobetrotter']){

    $id = $_POST['idGlobetrotter'];
    $globetrotter = new Globetrotter();
    $response = $globetrotter-> visualizzaProfilo($id);

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}

echo json_encode($response);

?>