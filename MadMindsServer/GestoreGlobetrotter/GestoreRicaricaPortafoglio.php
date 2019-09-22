<?php
require_once '../Globetrotter.php';

$response = array();
if($_POST['codGlobetrotter'] && $_POST['importo']){
    $codGlobetrotter = $_POST['codGlobetrotter'];
    $importo = $_POST['importo'];

    $globetrotter = new Globetrotter();
    $response['error'] = false;
    $ricarica = $globetrotter->ricaricaPortafoglio($codGlobetrotter, $importo);
    if($ricarica == true) {
        $response['error'] = false;
        $response['message'] = "Ricarica effettuata";
    } else {
        $response['error'] = true;
        $response['message'] = "Errore durante la ricarica";
    }

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>
