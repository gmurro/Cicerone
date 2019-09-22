<?php
require_once '../Attivita.php';

$response = array();
if($_POST['codAttivita']){
    $codAttivita = $_POST['codAttivita'];

    $attivita = new Attivita();
    $response = $attivita->listaPrenotati($codAttivita);

} else {
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>
