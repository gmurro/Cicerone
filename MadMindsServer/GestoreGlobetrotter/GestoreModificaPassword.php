<?php

require_once '../Globetrotter.php';

$response = array();


if($_POST['vecchiapsw'] && $_POST['nuovapsw'] && $_POST['idGlobetrotter'] ){

    $id = $_POST['idGlobetrotter'];
    $password= $_POST['vecchiapsw'];
    $nuovapassword = $_POST['nuovapsw'];


    $globetrotter = new Globetrotter();
    $response = $globetrotter-> modificaPasswordGlobetrotter($password, $nuovapassword,  $id );


} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";

}

echo json_encode($response);


?>
