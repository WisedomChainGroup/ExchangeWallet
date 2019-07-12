<?php

namespace Curve25519;


use StephenHill\Base58;
require 'Base58.php';

class keystore
{
    public $address;
    public $crypto;
    public $id;
    public $kdf;
    public $kdfparams;
    public $version;
    public $mac;

    //generate random private key
    function generatePrivateKey()
    {
        return random_bytes(32);
    }


    //generate public key from private key
    function generatePubkey($prvKey)
    {
        return publicKey($prvKey);
    }


    //generate public key Hash from public key
    function pubkeyToPubkeyhash($pubkey)
    {

        $pubkey256 =  hash('sha3-256',bin2hex($pubkey));
        $pubkey2566 =  hash('sha256',$pubkey);
//        $t = bin2hex($pubkey);
//        echo "$t<br>";
//        echo "$pubkey256<br>";
//        echo "$pubkey2566<br>";

        echo "$pubkey256<br>";
        $r1 = hash('ripemd160',hex2bin($pubkey256));
        echo "$r1<br>";
        return $r1;
    }

    //generate address from public key Hash
    function pubkeyHashToAddress($pubkeyHash)
    {
        $r2 = "00".$pubkeyHash;



        $a = hash('sha3-256',hex2bin($pubkeyHash));
        $r3 = hash('sha3-256',$a);
        $b4 = substr($r3,0,8);

//        echo "$r5<br>";

        $r5 = $r2.$b4;
//        echo "$r2<br>";
//        echo "$b4<br>";
//        echo "$r5<br>";
        $base58 = new Base58();
        $t = bin2hex($r5);
//        echo "$r5<br>";
//        echo "$t<br>";
        $s6 = $base58->encode(hex2bin($r5));
//        echo $s6;
        return $s6;

    }

    function addressToPubkeyHash($address){
        $base58 = new Base58();
        $r5 = $base58->decode($address);


//        byte[] r2 = ByteUtil.bytearraycopy(r5,0,21);
//        byte[] r1 = ByteUtil.bytearraycopy(r2,1,20);
//        String publickeyHash =  new String(Hex.encodeHex(r1));
//        return  publickeyHash;
    }

    //generate address from public key
    function generateaddress($pubkey){
        $keystore = new keystore();
        $r1 = $keystore->pubkeyToPubkeyhash($pubkey);
        $address = $keystore->pubkeyHashToAddress($r1);


        return $address;
    }

    function generatekeyatore($password)
    {
        $options = ['memory_cost' => 20480, 'time_cost' => 4, 'threads' => 2];
        $len = strlen($password);
        if ($len>20 || $len<8){
            return "invalid password!!!";
        }else {
            $privatekey = $this->generatekeyatore();
            $publickey = $this->generatePubkey($privatekey);
            $salt = random_bytes(32);
            $iv = random_bytes(16);
            $derivedKey = password_hash($password, PASSWORD_ARGON2ID,$options);
//            $aes  = new AES();
//            $cipherPrivKey = $aes.encrypt(,);
//            $mac_ = $derivedKey + $cipherPrivKey;
            $cipherparams = new cipherparams();
            $cipherparams->iv = $iv;

            $crypto = new crypto();
            $crypto->cipher  = "aes-256-ctr";
            $crypto->cipherparams  = $cipherparams;
//            $crypto->ciphertext  = $cipherPrivKey;

            $kdfparams = new kdfparams();
            $kdfparams->memoryCost ="20480";
            $kdfparams->parallelism="2";
            $kdfparams->timeCost = "4";
            $kdfparams->salt = $salt;

            $keystore = new keystore();
            $pubkeyhash = $this->pubkeyToPubkeyhash($publickey);
            $keystore->address = $this->pubkeyHashToAddress($pubkeyhash);
            $keystore->kdfparams = $kdfparams;
            $keystore->kdf = "argon2id";
            $keystore->crypto = $crypto;
            $json_keystore = json_encode($keystore, JSON_FORCE_OBJECT);
            return $json_keystore;
        }
    }
}

class crypto{
    public $cipher;
    public $cipherparams;
    public $ciphertext;
}

class cipherparams{
    public $iv;
}

class kdfparams{
    public $memoryCost;
    public $parallelism;
    public $salt;
    public $timeCost;
}







