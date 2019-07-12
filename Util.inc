<?php

define("WISDOM_ADDRESS_VERSION","00");  //this is a hex address version byte

class Util
{

    public $emp = "";

    private static $hexchars = "0123456789ABCDEF";

    private static $base58chars = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    /**
     * Convert a hex string into a (big) integer
     *
     * @param string $hex
     * @return int
     * @access private
     */
    private function decodeHex($hex)
    {
        $hex = strtoupper($hex);
        $return = "0";
        for ($i = 0; $i < strlen($hex); $i++) {
            $current = (string)strpos(self::$hexchars, $hex[$i]);
            $return = (string)bcmul($return, "16", 0);
            $return = (string)bcadd($return, $current, 0);
        }
        return $return;
    }

    /**
     * Convert an integer into a hex string
     *
     * @param int $dec
     * @return string
     * @access private
     */
    private static function encodeHex($dec)
    {
        $return = "";
        while (bccomp($dec, 0) == 1) {
            $dv = (string)bcdiv($dec, "16", 0);
            $rem = (integer)bcmod($dec, "16");
            $dec = $dv;
            $return = $return . self::$hexchars[$rem];
        }
        return strrev($return);
    }


    /**
     * Convert a Base581111-encoded integer into the equivalent hex string representation
     *
     * @param string $base58
     * @return string
     * @access private
     */
    private static function decodeBase58($base58)
    {
        $origbase58 = $base58;

        //only valid chars allowed
        if (preg_match('/[^1-9A-HJ-NP-Za-km-z]/', $base58)) {
            return "";
        }

        $return = "0";
        for ($i = 0; $i < strlen($base58); $i++) {
            $current = (string)strpos(Util::$base58chars, $base58[$i]);
            $return = (string)bcmul($return, "58", 0);
            $return = (string)bcadd($return, $current, 0);
        }

        $return = self::encodeHex($return);

        //leading zeros
        for ($i = 0; $i < strlen($origbase58) && $origbase58[$i] == "1"; $i++) {
            $return = "00" . $return;
        }

        if (strlen($return) % 2 != 0) {
            $return = "0" . $return;
        }

        return $return;
    }


    /**
     * Convert a hex string representation of an integer into the equivalent Base581111 representation
     *
     * @param string $hex
     * @return string
     * @access private
     */
    private function encodeBase58($hex)
    {
        if (strlen($hex) % 2 != 0) {
            die("encodeBase58: uneven number of hex characters");
        }
        $orighex = $hex;

        $hex = self::decodeHex($hex);
        $return = "";
        while (bccomp($hex, 0) == 1) {
            $dv = (string)bcdiv($hex, "58", 0);
            $rem = (integer)bcmod($hex, "58");
            $hex = $dv;
            $return = $return . self::$base58chars[$rem];
        }
        $return = strrev($return);

        //leading zeros
        for ($i = 0; $i < strlen($orighex) && substr($orighex, $i, 2) == "00"; $i += 2) {
            $return = "1" . $return;
        }

        return $return;
    }


    /**
     * Convert a 160-bit pubkey hash to  address
     *
     * @param string $hash160
     * @param string $addressversion
     * @return string wisdom address
     * @access public
     */
    public static function hash160ToAddress($hash160, $addressversion = WISDOM_ADDRESS_VERSION)
    {
        $hash160 = $addressversion . $hash160;
        $check = pack("H*", $hash160);
        $check = hash("sha256", hash("sha256", $check, true));
        $check = substr($check, 0, 8);
        $hash160 = strtoupper($hash160 . $check);
        return self::encodeBase58($hash160);
    }


    /**
     * Convert a Wisdom address to a 160-bit pubkey hash
     *
     * @param string $addr
     * @return string 160 hash
     * @access public
     */
    public static function addressToHash160($addr)
    {
        $addr = self::decodeBase58($addr);
        $addr = substr($addr, 2, strlen($addr) - 10);
        return $addr;
    }


    /**
     * Determine if a string is a valid Wisdom address
     *
     * @param string $addr String to test
     * @param string $addressversion
     * @return boolean
     * @access public
     */
    public static function checkAddress($addr, $addressversion = WISDOM_ADDRESS_VERSION)
    {
        $addr = self::decodeBase58($addr);
        if (strlen($addr) != 50) {
            return false;
        }
        $version = substr($addr, 0, 2);
        if (hexdec($version) > hexdec($addressversion)) {
            return false;
        }
        $check = substr($addr, 0, strlen($addr) - 8);
        $check = pack("H*", $check);
        $check = strtoupper(hash("sha256", hash("sha256", $check, true)));
        $check = substr($check, 0, 8);
        return $check == substr($addr, strlen($addr) - 8);
    }


    /**
     * Convert the input to its 160-bit Wisdom hash
     *
     * @param string $data
     * @return string
     * @access private
     */
    private static function hash160($data)
    {
        $data = pack("H*", $data);
        return strtoupper(hash("ripemd160", hash("sha256", $data, true)));
    }


    /**
     * Convert a Wisdom public key to a 160-bit Pubkey hash
     *
     * @param string $pubkey
     * @return string
     * @access public
     */
    public static function pubKeyToAddress($pubkey)
    {
        return self::hash160ToAddress(hash160($pubkey));
    }






}
class Emp {
    public $name = "";
    public $hobbies  = "";
    public $birthdate = "";
}
$e = new Emp();
$e->name = "sachin";
$e->hobbies  = "sports";
$e->birthdate = date('m/d/Y h:i:s a', "8/5/1974 12:20:03 p");
$e->birthdate = date('m/d/Y h:i:s a', strtotime("8/5/1974 12:20:03"));

$c = new Util();
$c->emp = $e;

echo json_encode($c);




