<?php

define("WISDOM_ADDRESS_VERSION","00");  //this is a hex address version byte

class Util
{


    private static $hexchars = "0123456789ABCDEF";

    private static $base58chars="123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    /**
     * Convert a hex string into a (big) integer
     *
     * @param string $hex
     * @return int
     * @access private
     */
    private function decodeHex($hex) {
        $hex = strtoupper($hex);
        $return = "0";
        for ($i = 0; $i < strlen($hex); $i++) {
            $current = (string) strpos(self::$hexchars, $hex[$i]);
            $return = (string) bcmul($return, "16", 0);
            $return = (string) bcadd($return, $current, 0);
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
    private static function encodeHex($dec) {
        $return = "";
        while (bccomp($dec, 0) == 1) {
            $dv = (string) bcdiv($dec, "16", 0);
            $rem = (integer) bcmod($dec, "16");
            $dec = $dv;
            $return = $return . self::$hexchars[$rem];
        }
        return strrev($return);
    }


    /**
     * Convert a Base58-encoded integer into the equivalent hex string representation
     *
     * @param string $base58
     * @return string
     * @access private
     */
    private static function decodeBase58($base58) {
        $origbase58 = $base58;

        //only valid chars allowed
        if (preg_match('/[^1-9A-HJ-NP-Za-km-z]/', $base58)) {
            return "";
        }

        $return = "0";
        for ($i = 0; $i < strlen($base58); $i++) {
            $current = (string) strpos(Util::$base58chars, $base58[$i]);
            $return = (string) bcmul($return, "58", 0);
            $return = (string) bcadd($return, $current, 0);
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
     * Convert a hex string representation of an integer into the equivalent Base58 representation
     *
     * @param string $hex
     * @return string
     * @access private
     */
    private function encodeBase58($hex) {
        if (strlen($hex) % 2 != 0) {
            die("encodeBase58: uneven number of hex characters");
        }
        $orighex = $hex;

        $hex = self::decodeHex($hex);
        $return = "";
        while (bccomp($hex, 0) == 1) {
            $dv = (string) bcdiv($hex, "58", 0);
            $rem = (integer) bcmod($hex, "58");
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
    public static function hash160ToAddress($hash160, $addressversion = WISDOM_ADDRESS_VERSION) {
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
    public static function addressToHash160($addr) {
        $addr = self::decodeBase58($addr);
        $addr = substr($addr, 2, strlen($addr) - 10);
        return $addr;
    }


}