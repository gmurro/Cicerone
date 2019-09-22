<?php
require_once '../Amministrazione.php';
if($_SERVER['REQUEST_METHOD']=='POST'){
    $nuovoSurplus = $_POST['nuovoSurplus'];
    $amministratore = new Amministrazione();

    if(isset($_POST['btInvia'])){
        $email = $amministratore->getEmail();
        $risposta = $amministratore->modificaSurplus($email,$nuovoSurplus);
        if($risposta == true){
            header("location: https://www.madminds.tk/VistaAmministrazione/VistaHome.php");
        }else {
           $messaggio="Modifica surplus non corretta";
            header("location: https://www.madminds.tk/VistaAmministrazione/VistaHome.php?messaggio={$messaggio}");
        }
    }
}
?>