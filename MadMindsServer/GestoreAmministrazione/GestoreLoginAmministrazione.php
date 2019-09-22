<?php
    require_once '../Amministrazione.php';
    if($_SERVER['REQUEST_METHOD']=='POST'){
        $email = $_POST['email'];
        $password = $_POST['password'];
        $amministratore = new Amministrazione();

        if(isset($_POST['btlogin'])){

            $risposta = $amministratore->isAmministrazioneEsistente($email,$password);
            if($risposta == true ){
                header('HTTP/1.1 301 Moved Permanently');
                header("location: http://www.madminds.tk/VistaAmministrazione/VistaHome.php?email={$email}");
            }
            else{
                $messaggio="email o password non corretti";
                header("Location: http://www.madminds.tk/VistaAmministrazione/VistaLogin.php?messaggio={$messaggio}");
            }
        }
    }
?>
