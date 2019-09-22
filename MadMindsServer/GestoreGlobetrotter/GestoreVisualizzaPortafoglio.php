<?php
require_once '../Globetrotter.php';

$response = array();
if($_POST['codGlobetrotter']){
    $codGlobetrotter = $_POST['codGlobetrotter'];

    $globetrotter = new Globetrotter();
    $response['error'] = false;
    $response['portafoglio'] = $globetrotter->visualizzaPortafoglio($codGlobetrotter);

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>