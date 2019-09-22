<?php

require_once '../Globetrotter.php';

$response = array();
if($_POST['email'] && $_POST['password']){
    $email = $_POST['email'];
    $password = $_POST['password'];

    $globetrotter = new Globetrotter();
    $response = $globetrotter->loginGlobetrotter($email, $password);

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>