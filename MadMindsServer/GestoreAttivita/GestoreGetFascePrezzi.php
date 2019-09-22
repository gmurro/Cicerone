<?php
require_once '../Attivita.php';

$response = array();
if($_POST['codAttivita'] ){
    $codAttivita = $_POST['codAttivita'];

    $attivita = new Attivita();
    $response =$attivita->getFasciePrezzi($codAttivita);

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}
header('Content-Type: application/json');
print json_encode($response);
?>