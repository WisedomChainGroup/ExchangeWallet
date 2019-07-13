<?php

namespace Curve25519;


use Aes\Aes;
use Keccak\Keccak256;
use StephenHill\Base58;
require 'Base58.php';
require 'Keccak256.inc';
require 'Aes.inc';

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
        $pubkey256 = Keccak256::hash(hex2bin(bin2hex($pubkey)),256);
        $r1 = hash('ripemd160',hex2bin($pubkey256));
        return $r1;
    }

    //generate address from public key Hash
    function pubkeyHashToAddress($pubkeyHash)
    {
        $r2 = "00".$pubkeyHash;
        $a = Keccak256::hash(hex2bin($pubkeyHash),256);
        $r3 = Keccak256::hash(hex2bin($a),256);
        $b4 = substr($r3,0,8);
        $r5 = $r2.$b4;
        $base58 = new Base58();
        $s6 = $base58->encode(hex2bin($r5));
        return $s6;

    }

    //地址转
    function addressToPubkeyHash($address){
        $base58 = new Base58();
        $ba58 = $base58->decode($address);
        $r5 = bin2hex($ba58);
        $r1 = substr($r5,1,41);
        return  $r1;
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
            $aes  = new Aes();
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

$key = new keystore();

$test = $key->generateaddress("00d9bfe14227dd19c10d4decd87c22fea52c5bd1b6bd9198cf");
echo $test;


//$r1 = hash('ripemd160',"aaaaaa");
//echo $r1;


$base58 = new Base58();
//$test = "00d0caa00d266fd6b615e325d503e95902dad2f6b8fc6f4f2a";
//$t = "1LrMVgJ1hLMNx8awpfDGiQZEi3UQYr4uQa";
//00d9bfe14227dd19c10d4decd87c22fea52c5bd1b6bd9198cf
//0d9bfe14227dd19c10d4decd87c22fea52c5bd1b
//b6bd9198cf
//1LrMVgJ1hLMNx8awpfDGiQZEi3UQYr4uQa


//
//$s6 = $base58->decode($test);
//$s7 = $base58->decode($t);
//$t2 = bin2hex($s7);
////echo "$s6<br>";
//echo "$t2";
//$pubkey = "54d786201936fedfbd168515026ea82fb53b8d77d994162aaa8f794a8e13051c";
//$pubkey256 = Keccak256::hash(hex2bin($pubkey),256);
//$pubkey256 = Keccak256::hash(hex2bin(bin2hex("aaaa")),256);
////$pubkey256 = Keccak256::hash(hex2bin($pubkey),256);
//echo $pubkey256;

