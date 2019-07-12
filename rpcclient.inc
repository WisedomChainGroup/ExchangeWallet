<?php



class WisdomClientException extends ErrorException {
    // Redefine the exception so message isn't optional
    public function __construct($message, $code = 0, $severity = E_USER_NOTICE, Exception $previous = null) {
        parent::__construct($message, $code, $severity, $previous);
    }

    public function __toString() {
        return __CLASS__ . ": [{$this->code}]: {$this->message}\n";
    }
}



require_once(dirname(__FILE__) . "/jsonrpc.inc");



class rpcclient extends jsonrpc_client
{
    public function can_connect(){

        return true;
    }
}