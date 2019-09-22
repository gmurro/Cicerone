<?php
require_once '../Cicerone.php';

$response = array();
if($_POST['codCicerone']){
    $codCicerone = $_POST['codCicerone'];

    $cicerone = new Cicerone();

    $response['error'] = false;
    $response['guadagno'] = round($cicerone->visualizzaGuadagno($codCicerone),2);

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>
